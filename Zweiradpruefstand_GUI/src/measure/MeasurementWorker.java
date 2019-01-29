package measure;

import data.Bike;
import data.Database;
import data.Config;
import data.PreDatapoint;
import data.DialData;
import data.RawDatapoint;
import development.TestCSV;
import logging.Logger;
import javax.swing.SwingWorker;
import main.BESDyno;
import main.BESDyno.MyTelegram;

/**
 *
 * @author emil
 */
public class MeasurementWorker extends SwingWorker<Object, DialData> {

    private static final Logger LOG = Logger.getLogger(MeasurementWorker.class.getName());

    private final BESDyno main = BESDyno.getInstance();
    private final Bike bike = Bike.getInstance();
    private final Database data = Database.getInstance();
    private final Calculate calc = new Calculate();
    private final Config config = Config.getInstance();
    private final MyTelegram telegram = BESDyno.getInstance().getTelegram();

    private Status status;

    public static enum Status {
        SHIFT_UP, WAIT, READY, MEASURE, FINISH
    };

    @Override
    protected Object doInBackground() {
        if (bike.isAutomatic()) {
            status = Status.WAIT;
        } else {
            status = Status.SHIFT_UP;
        }

        LOG.info("MeasurementWorker started...");
        while (!isCancelled()) {
            try {
                switch (status) {

                    case SHIFT_UP:
                        LOG.info("STATE: SHIFT_UP");
                        status = manageShiftUp();
                        break;

                    case WAIT:
                        LOG.info("STATE: WAIT");
                        status = manageWait((int) config.getHysteresisTime() / config.getPeriod());
                        break;

                    case READY:
                        LOG.info("STATE: READY");
                        status = manageReady();
                        break;

                    case MEASURE:
                        LOG.info("STATE: MEASURE");
                        status = manageMeasure();
                        break;

                    case FINISH:
                        LOG.info("Measurement finished");
                        manageFinish();
                        return null;

                    default:
                        throw new Exception("No Status...");
                }
            } catch (Exception ex) {
                LOG.severe(ex);
            }
        }
        return null;
    }

    //State-Methods
    //Lower than Start-Speed reached once -> hysteresis loop
    private Status manageShiftUp() throws Exception {
        main.addPendingRequest(telegram.start());

        //INIT -> time to get higher than Start-Speed
        for (int i = 0; i < 10; i++) {
            if (bike.isMeasRpm()) {
                publish(new DialData(Status.SHIFT_UP, measure(), config.getStartVelo(), config.getStartRpm()));
            } else {
                publish(new DialData(Status.SHIFT_UP, measureno(), config.getStartVelo()));
            }
        }

        if (bike.isMeasRpm()) {
            do {
                publish(new DialData(Status.SHIFT_UP, measure(), config.getStartVelo(), config.getStartRpm()));
            } while (data.getPreList().get(data.getPreList().size() - 1).getEngRpm() >= config.getStartRpm());
        } else {
            do {
                publish(new DialData(Status.SHIFT_UP, measureno(), config.getStartVelo()));
            } while (data.getVelList().get(data.getVelList().size() - 1) >= config.getStartVelo());
        }
        return Status.WAIT;
    }

    //Stabilize in Hysteresis loop -> ready
    private Status manageWait(int hysCount) throws Exception {
        if (bike.isMeasRpm()) {
            int hysteresisMin = config.getIdleRpm() - config.getHysteresisRpm();
            int hysteresisMax = config.getIdleRpm() + config.getHysteresisRpm();
            double rpm;
            int accepted = 0;
            do {
                publish(new DialData(Status.WAIT, measure(), config.getIdleVelo(), config.getIdleRpm()));
                rpm = data.getPreList().get(data.getPreList().size() - 1).getEngRpm();
                if (rpm > hysteresisMin && rpm < hysteresisMax) {
                    accepted++;
                    LOG.info("Hysteresis Loop: Try " + accepted + "/" + hysCount);
                }
            } while (accepted < hysCount);
        } else {
            int hysteresisMin = config.getIdleVelo() - config.getHysteresisVelo();
            int hysteresisMax = config.getIdleVelo() + config.getHysteresisVelo();
            double velocity;
            int accepted = 0;
            do {
                publish(new DialData(Status.WAIT, measureno(), config.getIdleVelo()));
                velocity = data.getVelList().get(data.getVelList().size() - 1);
                if (velocity > hysteresisMin && velocity < hysteresisMax) {
                    accepted++;
                }
            } while (accepted < hysCount);
        }
        return Status.READY;
    }

    //Greater than Start-Speed reached once -> start of measurement
    private Status manageReady() throws Exception {
        if (bike.isMeasRpm()) {
            do {
                publish(new DialData(Status.READY, measure(), config.getStartVelo(), config.getStartRpm()));
            } while (data.getPreList().get(data.getPreList().size() - 1).getEngRpm() <= config.getStartRpm());
        } else {
            do {
                publish(new DialData(Status.READY, measureno(), config.getStartVelo()));
            } while (data.getVelList().get(data.getVelList().size() - 1) <= config.getStartVelo());
        }
        return Status.MEASURE;
    }

    //Higher than Stop-Speed reached 5 times -> finish
    //Lower than Start-Speed reached 5 times -> finish
    private Status manageMeasure() throws Exception {
        //Clear all Lists for good Data ;)
        data.clearLists();

        if (bike.isStartStopMethod()) {
            if (bike.isMeasRpm()) {
                int stopCount = 0;
                do {
                    publish(new DialData(Status.MEASURE, measure(), config.getStopVelo(), config.getStopRpm()));
                    if (data.getPreList().get(data.getPreList().size() - 1).getEngRpm() >= config.getStopRpm()) {
                        stopCount++;
                    }
                } while (stopCount < 5);
            } else {
                int stopCount = 0;
                do {
                    publish(new DialData(Status.MEASURE, measureno(), config.getStopVelo()));
                    if (data.getVelList().get(data.getVelList().size() - 1) >= config.getStopRpm()) {
                        stopCount++;
                    }
                } while (stopCount < 5);
            }
        } else {
            if (bike.isMeasRpm()) {
                int stopCount = 0;
                do {
                    publish(new DialData(Status.MEASURE, measure(), config.getStartVelo(), config.getStartRpm()));
                    if (data.getPreList().get(data.getPreList().size() - 1).getEngRpm() <= config.getStartRpm()) {
                        stopCount++;
                    }
                } while (stopCount < 5);
            } else {
                int stopCount = 0;
                do {
                    publish(new DialData(Status.MEASURE, measureno(), config.getStartVelo()));
                    if (data.getVelList().get(data.getVelList().size() - 1) <= config.getStartVelo()) {
                        stopCount++;
                    }
                } while (stopCount < 5);
            }
        }
        return Status.FINISH;
    }

    //Calculates Power -> end of measurement
    private void manageFinish() {
        calc.calcPower();
        if (main.isTestMode()) {
            TestCSV csv = new TestCSV();
            csv.writeFiles();
        }
    }

    //Measurement-Methods
    public PreDatapoint measure() throws Exception {
        PreDatapoint pdp;

        main.addPendingRequest(telegram.measure());

        Thread.sleep(config.getPeriod());

        synchronized (Database.getInstance().getRawList()) {
            RawDatapoint rdp = data.getRawList().get(data.getRawList().size() - 1);

            LOG.debug("---->          Counts: " + rdp.getEngCount());
            LOG.debug("---->            Time: " + rdp.getTime());

            pdp = calc.calcRpm(rdp);
        }

        if (bike.isStartStopMethod()) {
            data.addPreDP(pdp);
        } else {

            if (pdp.getEngRpm() > data.getPreList().get(data.getPreList().size() - 1).getEngRpm()) {
                data.addPreDP(pdp);
            } else {
                data.addPreSchlepp(pdp);
            }

        }

        LOG.debug("---->   Motordrehzahl: " + data.getPreList().get(data.getPreList().size() - 1).getEngRpm());

        switch (config.getVelocity()) {
            case MPS:
                data.addVel(calc.calcMps(pdp));
                break;
            case KMH:
                data.addVel(calc.calcKmh(pdp));
                break;
            case MIH:
                data.addVel(calc.calcMih(pdp));
                break;
            default:
                throw new Exception("No Velocity Unit...");
        }

        LOG.debug("----> Geschwindigkeit: " + data.getVelList().get(data.getVelList().size() - 1));

        return pdp;
    }

    public double measureno() throws Exception {
        PreDatapoint pdp;

        main.addPendingRequest(telegram.measureno());

        Thread.sleep(config.getPeriod());

        synchronized (Database.getInstance().getRawList()) {
            pdp = calc.calcWheelOnly(data.getRawList().get(data.getRawList().size() - 1));
        }
        if (bike.isStartStopMethod()) {
            data.addPreDP(pdp);
        } else {
            if (pdp.getWheelRpm() > data.getPreList().get(data.getPreList().size() - 1).getWheelRpm()) {
                data.addPreDP(pdp);
            } else {
                data.addPreSchlepp(pdp);
            }
        }

        switch (config.getVelocity()) {
            case MPS:
                data.addVel(calc.calcMps(pdp));
                break;
            case KMH:
                data.addVel(calc.calcKmh(pdp));
                break;
            case MIH:
                data.addVel(calc.calcMih(pdp));
                break;
            default:
                throw new Exception("No Velocity Unit...");
        }

        return data.getVelList().get(data.getVelList().size() - 1);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
