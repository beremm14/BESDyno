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

    private final List<RawDatapoint> rawList;

    private final Bike bike = Bike.getInstance();
    private final BikePower power = BikePower.getInstance();
    private final Config config = Config.getInstance();

    public Calculate(List<RawDatapoint> rawList) {
        this.rawList = rawList;
    }

    public BikePower getBikePower() {
        return power;
    }

    public void calcRpm() {

        int totalImpulse = 20; //???
        int wheelRpm;
        int engRpm;

        for (int i = 0; i < rawList.size(); i++) {
            wheelRpm = (rawList.get(i).getWheelCount() / rawList.get(i).getTime() * totalImpulse);
            if (bike.isTwoStroke()) {
                engRpm = rawList.get(i).getEngCount() / rawList.get(i).getTime();
            } else {
                engRpm = (rawList.get(i).getEngCount() * 2) / rawList.get(i).getTime();
            }
            bike.add(new Datapoint(engRpm, wheelRpm, rawList.get(i).getTime()));
        }
    }

    public void calcPower() {
        boolean ps = config.isPs();
        
        //Wheel-Power
        for (int i = 0; i < bike.getDatalist().size(); i++) {
            double dOmega = 2 * Math.PI * bike.getDatalist().get(i).getWheelRpm();
            double alpha = dOmega / (bike.getDatalist().get(i).getTime() / 1000);
            double torque = config.getInertia() * alpha;
            double currPower = torque * dOmega;
            
            if(ps) {
                currPower = currPower * 1.359621617 / 1000;
            } else {
                currPower = currPower / 1000;
            }
            
            power.addWP(currPower);
        }

        //Engine-Power
        for (int i = 0; i < bike.getDatalist().size(); i++) {
            double dOmega = 2 * Math.PI * bike.getDatalist().get(i).getEngRpm();
            double alpha = dOmega / (bike.getDatalist().get(i).getTime() / 1000);
            double torque = config.getInertia() * alpha;
            double currPower = torque * dOmega;
            
            if(ps) {
                currPower = currPower * 1.359621617 / 1000;
            } else {
                currPower = currPower / 1000;
            }

            power.addEP(currPower);
        }
        
        //Max-Engine-Power
        power.setBikePower();
    }

}
