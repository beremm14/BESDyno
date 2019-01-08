package measure;

import data.Bike;
import data.Database;
import data.Config;
import data.Datapoint;
import data.RawDatapoint;

/**
 *
 * @author emil
 */
public class Calculate {

    private final Config config = Config.getInstance();
    private final Database data = new Database();

    //Calculates One Point
    public Datapoint calcRpm(RawDatapoint rdp) {
        int totalImpulse = 26;
        int wheelRpm;
        int engRpm;

        wheelRpm = (rdp.getWheelCount() / rdp.getTime() * totalImpulse);
        if (Bike.getInstance().isTwoStroke()) {
            engRpm = rdp.getEngCount() / rdp.getTime();
        } else {
            engRpm = (rdp.getEngCount() * 2) / rdp.getTime();
        }
        return new Datapoint(engRpm, wheelRpm, rdp.getTime());
    }

    public Datapoint calcWheelOnly(RawDatapoint rdp) {
        int totalImpulse = 26;
        return new Datapoint(rdp.getWheelCount() / rdp.getTime() * totalImpulse, rdp.getTime());
    }

    //Calculates One Point
    public double calcMps(Datapoint dp) {
        double r = 0.35;
        return r * 2 * Math.PI * (dp.getWheelRpm() / 60);
    }

    public double calcKmh(Datapoint dp) {
        return calcMps(dp) * 3.6;
    }

    public double calcMph(Datapoint dp) {
        return calcMps(dp) * 2.237;
    }

    //Calculates All
    public double calcPower() {
        
        //Engine
        for (int i = 0; i < data.getEngRpmList().size(); i++) {
            double dOmega = 2 * Math.PI * (data.getEngRpmList().get(i) / 60);
            double alpha = dOmega / (data.getTimeList().get(i) / 1000);
            double torque = config.getInertia() * alpha;
            double currPower = torque * dOmega;

            if (config.isPs()) {
                currPower = currPower * 1.359621617 / 1000;
            } else {
                currPower = currPower / 1000;
            }

            data.addEP(currPower);
            data.addET(torque);
        }

        //Wheel
        for (int i = 0; i < data.getWheelRpmList().size(); i++) {
            double dOmega = 2 * Math.PI * (data.getWheelRpmList().get(i) / 60);
            double alpha = dOmega / (data.getTimeList().get(i) / 1000);
            double torque = config.getInertia() * alpha;
            double currPower = torque * dOmega;

            if (config.isPs()) {
                currPower = currPower * 1.359621617 / 1000;
            } else {
                currPower = currPower / 1000;
            }

            data.addWP(currPower);
            data.addWT(torque);
        }

        //MAX-Power
        double power = data.getEngPowerList().get(0);
        for (Double p : data.getEngPowerList()) {
            if (p > power) {
                power = p;
            }
        }
        data.setBikePower(power);
        return power;
    }

}
