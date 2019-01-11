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
    private final Database data = new Database();

    //Calculates One Point
    public Datapoint calcRpm(RawDatapoint rdp) {
        int totalImpulse = 26;
        int wheelRpm;
        int engRpm;

        wheelRpm = (rdp.getWheelCount() / (rdp.getTime() * totalImpulse)) * 60000;
        if (Bike.getInstance().isTwoStroke()) {
            engRpm = (rdp.getEngCount() / rdp.getTime()) * 60000;
        } else {
            engRpm = ((rdp.getEngCount() * 2) / rdp.getTime()) * 60000;
        }
        return new Datapoint(engRpm, wheelRpm, rdp.getTime());
    }

    public Datapoint calcWheelOnly(RawDatapoint rdp) {
        int totalImpulse = 26;
        return new Datapoint(rdp.getWheelCount() / (rdp.getTime() * totalImpulse), rdp.getTime());
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
        return calcMps(dp) * 2.2369362920544;
    }

    //Calculates All
    public void calcPower() {

        //Engine
        for (int i = 0; i < data.getEngRpmList().size(); i++) {
            double dOmega = 2 * Math.PI * (data.getEngRpmList().get(i) / 60);
            double alpha = dOmega / (data.getTimeList().get(i) * 1000);
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
            double alpha = dOmega / (data.getTimeList().get(i) * 1000);
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
