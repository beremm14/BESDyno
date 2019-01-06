package measure;

import data.Bike;
import data.BikePower;
import data.Config;
import data.Datapoint;
import data.RawDatapoint;
import java.util.List;

/**
 *
 * @author emil
 */
public class Calculate {

    private final Config config = Config.getInstance();

    public Calculate() {
    }

    //Calculates All
    public void calcRpm(List<RawDatapoint> rawList) {

        int totalImpulse = 20; //???
        int wheelRpm;
        int engRpm;

        for (int i = 0; i < rawList.size(); i++) {
            wheelRpm = (rawList.get(i).getWheelCount() / rawList.get(i).getTime() * totalImpulse);
            if (Bike.getInstance().isTwoStroke()) {
                engRpm = rawList.get(i).getEngCount() / rawList.get(i).getTime();
            } else {
                engRpm = (rawList.get(i).getEngCount() * 2) / rawList.get(i).getTime();
            }
            Bike.getInstance().add(new Datapoint(engRpm, wheelRpm, rawList.get(i).getTime()));
        }
    }

    //Calculates One Point
    public Datapoint calcRpm(RawDatapoint rdp) {
        int totalImpulse = 20; //???
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
    
    
    //Calculates One Point
    public double calcMps(Datapoint dp) {
        double r = 0; //???
        return r * dp.getWheelRpm() * 0.10472;
    }
    
    public double calcKmh(Datapoint dp) {
        double r = 0; //???
        return r * dp.getWheelRpm() * 0.10472 * 3.6;
    }
    
    public double calcMph(Datapoint dp) {
        double r = 0; //???
        return r * dp.getWheelRpm() * 0.10472 * 2.237;
    }

    
    //Calculates All
    public void calcPower() {
        boolean ps = config.isPs();

        //Wheel-Power
        for (int i = 0; i < Bike.getInstance().getDatalist().size(); i++) {
            double dOmega = 2 * Math.PI * Bike.getInstance().getDatalist().get(i).getWheelRpm();
            double alpha = dOmega / (Bike.getInstance().getDatalist().get(i).getTime() / 1000);
            double torque = config.getInertia() * alpha;
            double currPower = torque * dOmega;

            if (ps) {
                currPower = currPower * 1.359621617 / 1000;
            } else {
                currPower = currPower / 1000;
            }

            BikePower.getInstance().addWP(currPower);
        }

        //Engine-Power
        for (int i = 0; i < Bike.getInstance().getDatalist().size(); i++) {
            double dOmega = 2 * Math.PI * Bike.getInstance().getDatalist().get(i).getEngRpm();
            double alpha = dOmega / (Bike.getInstance().getDatalist().get(i).getTime() / 1000);
            double torque = config.getInertia() * alpha;
            double currPower = torque * dOmega;

            if (ps) {
                currPower = currPower * 1.359621617 / 1000;
            } else {
                currPower = currPower / 1000;
            }

            BikePower.getInstance().addEP(currPower);
        }

        //Max-Engine-Power
        BikePower.getInstance().setBikePower();
    }

}
