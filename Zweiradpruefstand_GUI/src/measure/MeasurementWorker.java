package measure;

import data.Bike;
import data.BikePower;
import data.Config;
import data.Datapoint;
import data.DialData;
import javax.swing.SwingWorker;
import main.BESDyno;

/**
 *
 * @author emil
 */
public class MeasurementWorker extends SwingWorker<Object, DialData> {

    private final BESDyno main = BESDyno.getInstance();
    private final Bike bike = Bike.getInstance();
    private final BikePower power = BikePower.getInstance();
    private final Calculate calc = new Calculate();
    private final Config config = Config.getInstance();
    private final BESDyno.MyTelegram telegram = BESDyno.getInstance().getTelegram();

    private Status status;

    public enum Status {
        SHIFT_UP, READY, START, STOP, AUTOMATIC
    };

    @Override
    protected Object doInBackground() throws Exception {

        //Automatic-Bikes
        if (bike.isAutomatic()) {
            status = Status.AUTOMATIC;

            if (bike.isMeasRpm()) {
                int stopCount = 0;
                do {
                    main.addPendingRequest(telegram.measure());

                    Datapoint dp = calc.calcRpm(power.getRawList().get(power.getRawList().size()-1));
                    power.addWR(dp.getWheelRpm());
                    power.addER(dp.getEngRpm());

                    switch (config.getVelocity()) {
                        case MPS:
                            power.addVel(calc.calcMps(dp));
                            break;
                        case KMH:
                            power.addVel(calc.calcKmh(dp));
                            break;
                        case MPH:
                            power.addVel(calc.calcMph(dp));
                            break;
                        default:
                            throw new Exception("No Velocity Unit...");
                    }
                    publish(new DialData(dp));
                    
                    if (power.getVelList().get(power.getVelList().size()-1) < config.getStartKmh() ||
                        power.getEngRpm().get(power.getEngRpm().size()-1) < config.getStartRpm()) {
                        stopCount++;
                    }
                } while (stopCount < 5);
            } else {
                int stopCount = 0;
                do {
                    main.addPendingRequest(telegram.measureno());

                            Datapoint dp = calc.calcRpm(power.getRawList().get(power.getRawList().size()-1));
                            power.addWR(dp.getWheelRpm());

                            switch (config.getVelocity()) {
                                case MPS:
                                    power.addVel(calc.calcMps(dp));
                                    break;
                                case KMH:
                                    power.addVel(calc.calcKmh(dp));
                                    break;
                                case MPH:
                                    power.addVel(calc.calcMph(dp));
                                    break;
                                default:
                                    throw new Exception("No Velocity Unit...");
                            }
                    publish(new DialData(power.getVelList().get(power.getVelList().size()-1)));
                    
                    if (power.getVelList().get(power.getVelList().size()-1) < config.getStartKmh()) {
                        stopCount++;
                    }
                } while (stopCount < 5);
            }

            //Manual-Bikes
        } else {

            switch (status) {

                case SHIFT_UP:
                    if (bike.isMeasRpm()) {
                        do {
                            main.addPendingRequest(telegram.measure());

                            Datapoint dp = calc.calcRpm(power.getRawList().get(power.getRawList().size()-1));
                            power.addWR(dp.getWheelRpm());
                            power.addER(dp.getEngRpm());

                            switch (config.getVelocity()) {
                                case MPS:
                                    power.addVel(calc.calcMps(dp));
                                    break;
                                case KMH:
                                    power.addVel(calc.calcKmh(dp));
                                    break;
                                case MPH:
                                    power.addVel(calc.calcMph(dp));
                                    break;
                                default:
                                    throw new Exception("No Velocity Unit...");
                            }
                            publish(new DialData(dp));
                        } while (power.getVelList().get(power.getVelList().size()-1) <= config.getIdleKmh()
                                || power.getEngRpm().get(power.getEngRpm().size()-1) <= config.getIdleRpm());
                    } else {
                        do {
                            main.addPendingRequest(telegram.measureno());

                            Datapoint dp = calc.calcRpm(power.getRawList().get(power.getRawList().size()-1));
                            power.addWR(dp.getWheelRpm());

                            switch (config.getVelocity()) {
                                case MPS:
                                    power.addVel(calc.calcMps(dp));
                                    break;
                                case KMH:
                                    power.addVel(calc.calcKmh(dp));
                                    break;
                                case MPH:
                                    power.addVel(calc.calcMph(dp));
                                    break;
                                default:
                                    throw new Exception("No Velocity Unit...");
                            }
                            publish(new DialData(power.getVelList().get(power.getVelList().size()-1)));
                        } while (power.getVelList().get(power.getVelList().size()-1) < config.getIdleKmh());
                    }
                    break;

                case READY:
                    if (bike.isMeasRpm()) {

                    }
                    break;

                case START:
                    break;

                case STOP:
                    break;

                default:
                    throw new Exception("No Status...");
            }
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
