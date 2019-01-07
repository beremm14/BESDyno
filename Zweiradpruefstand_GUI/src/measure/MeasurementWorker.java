package measure;

import data.Bike;
import data.BikePower;
import data.Config;
import data.DialData;
import javax.swing.SwingWorker;

/**
 *
 * @author emil
 */
public class MeasurementWorker extends SwingWorker<Object, DialData> {

    private final Bike bike = Bike.getInstance();
    private final BikePower power = BikePower.getInstance();
    private final Config config = Config.getInstance();
    private final MeasurementManager manager = new MeasurementManager();

    private Status status;

    public enum Status {
        SHIFT_UP, READY, START, STOP, AUTOMATIC
    };

    @Override
    protected Object doInBackground() throws Exception {

        switch (status) {

            case AUTOMATIC:
                if (bike.isMeasRpm()) {
                    int stopCount = 0;
                    do {
                        publish(manager.measure());
                        if (power.getVelList().get(power.getVelList().size() - 1) < config.getStartKmh()
                            || power.getEngRpm().get(power.getEngRpm().size() - 1) < config.getStartRpm()) {
                            stopCount++;
                        }
                    } while (stopCount < 5);
                } else {
                    int stopCount = 0;
                    do {
                        publish(manager.measureno());
                        if (power.getVelList().get(power.getVelList().size() - 1) < config.getStartKmh()) {
                            stopCount++;
                        }
                    } while (stopCount < 5);
                }
                break;

            case SHIFT_UP:
                if (bike.isMeasRpm()) {
                    do {
                        publish(manager.measure());
                    } while (power.getVelList().get(power.getVelList().size() - 1) <= config.getIdleKmh()
                             || power.getEngRpm().get(power.getEngRpm().size() - 1) <= config.getIdleRpm());
                } else {
                    do {
                        publish(manager.measureno());
                    } while (power.getVelList().get(power.getVelList().size() - 1) < config.getIdleKmh());
                }
                status = Status.READY;
                break;

            case READY:
                if (bike.isMeasRpm()) {
                    
                }
                status = Status.START;
                break;

            case START:
                break;

            case STOP:
                break;

            default:
                throw new Exception("No Status...");
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
