package measure;

import data.Bike;
import data.Database;
import data.Config;
import data.Datapoint;
import data.DialData;
import logging.Logger;
import javax.swing.SwingWorker;
import main.BESDyno;
import main.BESDyno.MyTelegram;

/**
 *
 * @author emil
 */
public class MeasurementWorker extends SwingWorker<Object, DialData> {
    
    /****************************************************************
     *  Ablauf des Messvorgangs:                                    *
     *                                                              *
     *  1) Hochschalten in den letzten Gang und abfallen lassen     *
     *     -> ab Erreichen der Startgeschwindigkeit: Einpendeln     *
     *  2) Einpendeln um die Leerlaufgeschwindigkeit                *
     *     -> solange wie HysteresisTime                            *
     *  3) Gas geben                                                *
     *     -> ab Erreichen der Start-Geschwindigkeit: Messen        *
     *  4) Bei Höchstdrehzahl abfallen lassen                       *
     *     -> ab wieder Erreichn der Start-Geschwindigkeit: Stoppen *
     *                                                              *
     *  Anmerkung: Bei Automatik-Zweirädern ist der                 *
     *  erste State SHIFT_UP nicht notwendig.                       *
     ****************************************************************/
    
    private static final Logger LOG = Logger.getLogger(MeasurementWorker.class.getName());

    private final BESDyno main = BESDyno.getInstance();
    private final Bike bike = Bike.getInstance();
    private final Database data = Database.getInstance();
    private final Calculate calc = new Calculate();
    private final Config config = Config.getInstance();
    private final MyTelegram telegram = BESDyno.getInstance().getTelegram();

    private Status status;

    public static enum Status {
        SHIFT_UP, WAIT, READY, MEASURE, FINISH, FINISHED
    };

    @Override
    protected Object doInBackground() {
        try {
            while (!isCancelled()) {
                switch (status) {

                    case SHIFT_UP:
                        status = manageShiftUp();
                        break;
                        
                    case WAIT:
                        status = manageWait((int) config.getHysteresisTime()/config.getPeriod());
                        break;

                    case READY:
                        break;

                    case MEASURE:
                        break;

                    case FINISH:
                        break;
                        
                    case FINISHED:
                        return null;

                    default:
                        throw new Exception("No Status...");
                }
            }
        } catch (Exception ex) {
            LOG.severe(ex);
        }
        return null;
    }
    
    
    //State-Methods
    
    //Lower than Start-Speed reached once -> hysteresis loop
    private Status manageShiftUp() throws Exception {
        if (bike.isMeasRpm()) {
            do {
                publish(measure());
            } while (data.getEngRpmList().get(data.getEngRpmList().size()-1) <= config.getStartRpm());
        } else {
            do {
                publish(measureno());
            } while (data.getVelList().get(data.getVelList().size()-1) <= config.getStartVelo());
        }
        return Status.WAIT;
    }
    
    //Commute into Hysteresis -> ready
    private Status manageWait(int hysCount) throws Exception {
        if (bike.isMeasRpm()) {
            int hysteresisMin = config.getIdleRpm() - config.getHysteresisRpm();
            int hysteresisMax = config.getIdleRpm() + config.getHysteresisRpm();
            int rpm;
            int accepted = 0;
            do {
                publish(measure());
                rpm = data.getEngRpmList().get(data.getEngRpmList().size()-1);
                if (rpm > hysteresisMin && rpm < hysteresisMax) {
                    accepted++;
                }
                Thread.sleep(config.getPeriod());
            } while (accepted < hysCount);
        } else {
            int hysteresisMin = config.getIdleVelo() - config.getHysteresisVelo();
            int hysteresisMax = config.getIdleVelo() + config.getHysteresisVelo();
            double velocity;
            int accepted = 0;
            do {
                publish(measure());
                velocity = data.getVelList().get(data.getVelList().size()-1);
                if (velocity > hysteresisMin && velocity < hysteresisMax) {
                    accepted++;
                }
                Thread.sleep(config.getPeriod());
            } while (accepted < hysCount);
        }
        return Status.READY;
    }
    
    //Greater than Start-Speed reached once -> start of measurement
    private Status manageReady() throws Exception {
        if (bike.isMeasRpm()) {
            do {
                publish(measure());
            } while (data.getEngRpmList().get(data.getEngRpmList().size()-1) >= config.getStartRpm());
        } else {
            do {
                publish(measureno());
            } while (data.getVelList().get(data.getVelList().size()-1) >= config.getStartVelo());
        }
        return Status.MEASURE;
    }
    
    //Lower than Start-Speed reached 5 times -> finish
    private Status manageMeasure() throws Exception {
        //Clear all Lists for good Data ;)
        data.clearLists();
        
        if (bike.isMeasRpm()) {
            int stopCount = 0;
            do {
                publish(measure());
                if (data.getEngRpmList().get(data.getEngRpmList().size()-1) <= config.getStartRpm()) {
                    stopCount++;
                }
            } while (stopCount < 5);
        } else {
            int stopCount = 0;
            do {
                publish(measureno());
                if (data.getVelList().get(data.getVelList().size()-1) <= config.getStartVelo()) {
                    stopCount++;
                }
            } while (stopCount < 5);
        }
        return Status.FINISH;
    }
    
    //Calculates Power -> end of measurement
    private Status manageFinish() {
        
        return Status.FINISHED;
    }
    
    
    //Measurement-Methods
    public DialData measure() throws Exception {
        Datapoint dp;

        main.addPendingRequest(telegram.measure());

        synchronized (data.syncObj) {

            data.syncObj.wait(1000);

            dp = calc.calcRpm(data.getRawList().get(data.getRawList().size() - 1));
            data.addWR(dp.getWheelRpm());
            data.addER(dp.getEngRpm());

            switch (config.getVelocity()) {
                case MPS:
                    data.addVel(calc.calcMps(dp));
                    break;
                case KMH:
                    data.addVel(calc.calcKmh(dp));
                    break;
                case MPH:
                    data.addVel(calc.calcMph(dp));
                    break;
                default:
                    throw new Exception("No Velocity Unit...");
            }
        }

        return new DialData(dp);
    }

    public DialData measureno() throws Exception {
        Datapoint dp;

        main.addPendingRequest(telegram.measureno());

        synchronized (data.syncObj) {
            data.syncObj.wait(1000);

            dp = calc.calcRpm(data.getRawList().get(data.getRawList().size() - 1));
            data.addWR(dp.getWheelRpm());

            switch (config.getVelocity()) {
                case MPS:
                    data.addVel(calc.calcMps(dp));
                    break;
                case KMH:
                    data.addVel(calc.calcKmh(dp));
                    break;
                case MPH:
                    data.addVel(calc.calcMph(dp));
                    break;
                default:
                    throw new Exception("No Velocity Unit...");
            }
        }

        return new DialData(data.getVelList().get(data.getVelList().size() - 1));
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
