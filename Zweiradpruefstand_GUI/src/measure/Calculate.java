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

    private List<RawDatapoint> rawList;
    
    private Bike bike = Bike.getInstance();
    private BikePower power = BikePower.getInstance();
    private Config config = Config.getInstance();

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
        //Wheel-Power
        for (int i = 0; i < bike.getDatalist().size(); i++) {
            
            double dOmega = 2 * Math.PI * bike.getDatalist().get(i).getWheelRpm();
            double alpha = dOmega / (bike.getDatalist().get(i).getTime() / 1000);
            double torque = config.getInertia() * alpha;
            double currPower = torque * dOmega;
            
            power.addWP(currPower);
        }
    }

    public BikePower getPower() {
        return power;
    }

    public Bike getBike() {
        return bike;
    }

}
