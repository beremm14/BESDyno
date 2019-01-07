package measure;

import data.Bike;
import data.BikePower;
import data.Config;
import data.DialData;
import logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author emil
 */
public class MeasurementWorker extends SwingWorker<Object, DialData> {
    
    /*
    WAIT:           Waits hysteresisTime milliseconds (security: no possibility to get lower than IDLE
    SHIFT_UP:       Ends, if velocity is lower than IDLE
    READY:          Ends, if velocity is higher than START
    MEASURE:        Data for measurement; ends, if velocity is lower than IDLE
    STOP:           Calculates power
    NO_MEASUREMENT: Nothing to do...
    */

    private static final Logger LOG = Logger.getLogger(MeasurementWorker.class.getName());

    private final Bike bike = Bike.getInstance();
    private final BikePower power = BikePower.getInstance();
    private final Config config = Config.getInstance();
    private final MeasurementManager manager = new MeasurementManager();

    private Status status;

    public enum Status {
        WAIT, SHIFT_UP, READY, MEASURE, FINISH, NO_MEASUREMENT
    };

    @Override
    protected Object doInBackground() {
        try {
            while (!isCancelled()) {
                switch (status) {

                    case WAIT:
                        Thread.sleep(config.getHysteresisTime());
                        break;

                    case SHIFT_UP:
                        if (bike.isMeasRpm()) {
                            int stopCount = 0;
                            do {
                                publish(manager.measure());
                                if (power.getVelList().get(power.getVelList().size() - 1) <= config.getIdleKmh()
                                    || power.getEngRpm().get(power.getEngRpm().size() - 1) <= config.getIdleRpm()) {
                                    stopCount++;
                                }
                            } while (stopCount < 3);
                        } else {
                            int stopCount = 0;
                            do {
                                publish(manager.measureno());
                                if (power.getVelList().get(power.getVelList().size() - 1) < config.getIdleKmh()) {
                                    stopCount++;
                                }
                            } while (stopCount < 3);
                        }
                        status = Status.READY;
                        break;

                    case READY:
                        if (bike.isMeasRpm()) {
                            int startCount = 0;
                            do {
                                publish(manager.measure());
                                if (power.getVelList().get(power.getVelList().size() - 1) >= config.getStartKmh()
                                        || power.getEngRpm().get(power.getEngRpm().size() - 1) >= config.getStartRpm()) {
                                    startCount++;
                                }
                            } while (startCount < 3);
                        } else {
                            int startCount = 0;
                            do {
                                publish(manager.measureno());
                                if (power.getVelList().get(power.getVelList().size() - 1) >= config.getStartKmh()) {
                                    startCount++;
                                }
                            } while (startCount < 3);
                        }
                        status = Status.MEASURE;
                        break;

                    case MEASURE:
                        if (bike.isMeasRpm()) {
                            int stopCount = 0;
                            do {
                                publish(manager.measure());
                                if (power.getVelList().get(power.getVelList().size() - 1) <= config.getIdleKmh()
                                        || power.getEngRpm().get(power.getEngRpm().size() - 1) <= config.getIdleRpm()) {
                                    stopCount++;
                                }
                            } while (stopCount > 5);
                        } else {
                            int stopCount = 0;
                            do {
                                publish(manager.measureno());
                                if (power.getVelList().get(power.getVelList().size() - 1) <= config.getIdleKmh()) {
                                    stopCount++;
                                }
                            } while (stopCount < 5);
                        }
                        status = Status.FINISH;
                        break;

                    case FINISH:
                        break;
                        
                    case NO_MEASUREMENT:
                        break;

                    default:
                        throw new Exception("No Status...");
                }
            }
        } catch (Exception ex) {
            LOG.severe(ex);
        }
        return null;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
