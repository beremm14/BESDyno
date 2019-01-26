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

    private final Bike bike = Bike.getInstance();
    private final Config config = Config.getInstance();
    private final Database data = Database.getInstance();

    //Calculates One Point
    public Datapoint calcRpm(RawDatapoint rdp) {
        double engCount = (double) rdp.getEngCount();
        double wheelCount = (double) rdp.getWheelCount();
        double time = (double) rdp.getTime() / 1000.0;

        double totalImpulse = 26.0;
        double engRpm;
        double wheelRpm;

        wheelRpm = (wheelCount / (time * totalImpulse)) * 60000.0;

        if (Bike.getInstance().isTwoStroke()) {
            engRpm = (engCount / time) * 60000.0;
        } else {
            engRpm = ((engCount * 2.0) / time) * 60000.0;
        }

        return new Datapoint(engRpm, wheelRpm, (((double)rdp.getTime()) / 1000.0));
    }

    public Datapoint calcWheelOnly(RawDatapoint rdp) {
        double totalImpulse = 26.0;
        double wheelCount = (double) rdp.getWheelCount();
        double time = (double) rdp.getTime() / 1000.0;

        double wheelRpm = (wheelCount / (time * totalImpulse)) * 60000.0;

        return new Datapoint(wheelRpm, (((double)rdp.getTime()) / 1000.0));
    }

    //Calculates One Point
    public double calcMps(Datapoint dp) {
        double r = 0.35;
        double wheelRpm = (double) dp.getWheelRpm();
        return r * 2.0 * Math.PI * (wheelRpm / 60.0);
    }

    public double calcKmh(Datapoint dp) {
        return calcMps(dp) * 3.6;
    }

    public double calcMph(Datapoint dp) {
        return calcMps(dp) * 2.2369362920544;
    }

    //Calculates All
    public void calcPower() {

        //Engine
        for (int i = 0; i < data.getEngRpmList().size(); i++) {
            double dOmega = 2 * Math.PI * ((double) data.getEngRpmList().get(i) / 60.0);
            double alpha = dOmega / ((double) data.getRawList().get(i).getTime() * 1000.0);
            double torque = config.getInertia() * alpha;
            double currPower = torque * dOmega;

            if (config.isPs()) {
                currPower = currPower * 1.359621617 / 1000.0;
            } else {
                currPower = currPower / 1000.0;
            }

            data.addEP(currPower);
            data.addET(torque);
        }

        //Wheel
        for (int i = 0; i < data.getWheelRpmList().size(); i++) {
            double dOmega = 2 * Math.PI * ((double) data.getWheelRpmList().get(i) / 60.0);
            double alpha = dOmega / ((double) data.getRawList().get(i).getTime() * 1000.0);
            double torque = config.getInertia() * alpha;
            double currPower = torque * dOmega;

            if (config.isPs()) {
                currPower = currPower * 1.359621617 / 1000.0;
            } else {
                currPower = currPower / 1000.0;
            }

            data.addWP(currPower);
            data.addWT(torque);
        }

        //MAX-Power
        if (bike.isMeasRpm()) {
            double power = data.getEngPowerList().get(0);
            for (Double p : data.getEngPowerList()) {
                if (p > power) {
                    power = p;
                }
            }
            data.setBikePower(power);
        } else {
            double power = data.getWheelPowerList().get(0);
            for (Double p : data.getWheelPowerList()) {
                if (p > power) {
                    power = p;
                }
            }
            data.setBikePower(power);
        }

        //MAX-Velocity
        double velo = data.getVelList().get(0);
        for (Double v : data.getVelList()) {
            if (v > velo) {
                velo = v;
            }
        }
        data.setBikeVelo(velo);

        //MAX-Torque
        if (bike.isMeasRpm()) {
            double torque = data.getEngTorList().get(0);
            for (Double m : data.getEngTorList()) {
                if (m > torque) {
                    torque = m;
                }
            }
            data.setBikeTorque(torque);
        } else {
            double torque = data.getWheelTorList().get(0);
            for (Double m : data.getWheelTorList()) {
                if (m > torque) {
                    torque = m;
                }
            }
            data.setBikeTorque(torque);
        }
    }

}
