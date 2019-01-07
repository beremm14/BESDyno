package measure;

import data.BikePower;
import data.Config;
import data.Datapoint;
import data.DialData;
import main.BESDyno;
import main.BESDyno.MyTelegram;

/**
 *
 * @author emil
 */
public class MeasurementManager {

    BESDyno main = BESDyno.getInstance();
    BikePower power = BikePower.getInstance();
    Calculate calc = new Calculate();
    Config config = Config.getInstance();
    MyTelegram telegram = BESDyno.getInstance().getTelegram();

    public DialData measure() throws Exception {
        Datapoint dp;

        main.addPendingRequest(telegram.measure());

        synchronized (power.syncObj) {

            power.syncObj.wait(1000);

            dp = calc.calcRpm(power.getRawList().get(power.getRawList().size() - 1));
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
        }

        return new DialData(dp);
    }

    public DialData measureno() throws Exception {
        Datapoint dp;

        main.addPendingRequest(telegram.measureno());

        synchronized (power.syncObj) {
            power.syncObj.wait(1000);

            dp = calc.calcRpm(power.getRawList().get(power.getRawList().size() - 1));
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
        }

        return new DialData(power.getVelList().get(power.getVelList().size() - 1));
    }

}
