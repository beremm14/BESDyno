package measure;

import data.Bike;
import data.Database;
import data.Config;
import data.Datapoint;
import data.RawDatapoint;
import java.util.List;

/**
 *
 * @author emil
 */
public class Calculate {

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
    
    public Datapoint calcWheelOnly(RawDatapoint rdp) {
        int totalImpulse = 20; //???
        return new Datapoint(rdp.getWheelCount() / rdp.getTime() * totalImpulse, rdp.getTime());
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
//        boolean ps = Config.getInstance().isPs();
//
//        //Wheel-Power
//        for (int i = 0; i < Bike.getInstance().getDatalist().size(); i++) {
//            double dOmega = 2 * Math.PI * Bike.getInstance().getDatalist().get(i).getWheelRpm();
//            double alpha = dOmega / (Bike.getInstance().getDatalist().get(i).getTime() / 1000);
//            double torque = Config.getInstance().getInertia() * alpha;
//            double currPower = torque * dOmega;
//
//            if (ps) {
//                currPower = currPower * 1.359621617 / 1000;
//            } else {
//                currPower = currPower / 1000;
//            }
//
//            Database.getInstance().addWP(currPower);
//        }
//
//        //Engine-Power
//        for (int i = 0; i < Bike.getInstance().getDatalist().size(); i++) {
//            double dOmega = 2 * Math.PI * Bike.getInstance().getDatalist().get(i).getEngRpm();
//            double alpha = dOmega / (Bike.getInstance().getDatalist().get(i).getTime() / 1000);
//            double torque = Config.getInstance().getInertia() * alpha;
//            double currPower = torque * dOmega;
//
//            if (ps) {
//                currPower = currPower * 1.359621617 / 1000;
//            } else {
//                currPower = currPower / 1000;
//            }
//
//            Database.getInstance().addEP(currPower);
//        }

        //Max-Engine-Power
        Database.getInstance().setBikePower();
        
        
        
    }

}
