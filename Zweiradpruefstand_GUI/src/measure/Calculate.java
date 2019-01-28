package measure;

import data.Bike;
import data.Database;
import data.Config;
import data.Datapoint;
import data.PreDatapoint;
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
    public PreDatapoint calcRpm(RawDatapoint rdp) {
        double engCount = (double) rdp.getEngCount();
        double wheelCount = (double) rdp.getWheelCount();
        double time = (double) rdp.getTime() / 1000.0; //ms

        double totalImpulse = 26.0;
        double engRpm;
        double wheelRpm;

        wheelRpm = (wheelCount / (time * totalImpulse)) * 60000.0;

        if (Bike.getInstance().isTwoStroke()) {
            engRpm = (engCount / time) * 60000.0;
        } else {
            engRpm = ((engCount * 2.0) / time) * 60000.0;
        }

        return new PreDatapoint(engRpm, wheelRpm, (((double) rdp.getTime()) / 1000.0));
    }

    public PreDatapoint calcWheelOnly(RawDatapoint rdp) {
        double totalImpulse = 26.0;
        double wheelCount = (double) rdp.getWheelCount();
        double time = (double) rdp.getTime() / 1000.0;

        double wheelRpm = (wheelCount / (time * totalImpulse)) * 60000.0;

        return new PreDatapoint(wheelRpm, (((double) rdp.getTime()) / 1000.0));
    }

    //Calculates One Point
    public double calcMps(PreDatapoint pdp) {
        return 0.35 * Math.PI * (pdp.getWheelRpm() / 60.0);
    }

    public double calcKmh(PreDatapoint pdp) {
        return calcMps(pdp) * 3.6;
    }

    public double calcMih(PreDatapoint pdp) {
        return calcMps(pdp) * 2.2369362920544;
    }

    public void calcPower() {

        //Calculation without Schlepp-Power
        if (bike.isStartStopMethod()) {

            double lastOmega = 0;

            for (PreDatapoint pdp : data.getPreList()) {
                double omega = (2 * Math.PI / 60) * pdp.getWheelRpm();
                double dOmega = omega - lastOmega;
                double alpha = dOmega / pdp.getTime();
                double wheelPower = omega * alpha * config.getInertia();

                data.addDP(new Datapoint(wheelPower, omega));

                lastOmega = omega;
            }

         //Calculation within Schlepp-Power
        } else {
            
        }

        //Evaluation of Maximum-Values
        data.rmFirstDP();

        //Torque and Power
        double maxTorque = data.getDataList().get(0).getTorque();
        double maxPower = data.getDataList().get(0).getPower();
        for (Datapoint dp : data.getDataList()) {
            if (dp.getTorque() > maxTorque) {
                maxTorque = dp.getTorque();
            }
            if (dp.getPower() > maxPower) {
                maxPower = dp.getPower();
            }
        }
        data.setBikePower(maxPower);
        data.setBikeTorque(maxTorque);

        //Velocity
        double maxVelocity = calcMps(data.getPreList().get(0));
        for (PreDatapoint pdp : data.getPreList()) {
            if (calcMps(pdp) > maxVelocity) {
                maxVelocity = calcMps(pdp);
            }
        }
        switch (config.getVelocity()) {
            case MPS:
                data.setBikeVelo(maxVelocity);
                break;
            case MIH:
                data.setBikeVelo(maxVelocity * 2.2369362920544);
                break;
            case KMH:
                data.setBikeVelo(maxVelocity * 3.6);
                break;
        }

    }

}
