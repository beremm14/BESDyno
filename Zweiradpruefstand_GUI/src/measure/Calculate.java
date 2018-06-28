package measure;

import data.Bike;
import data.BikePower;
import data.Config;
import data.RawDatapoint;
import java.util.List;

/**
 *
 * @author emil
 */
public class Calculate {
    
    private List<RawDatapoint> rawList;
    
    private Bike bike = new Bike();
    private BikePower power = new BikePower();
    private Config config = new Config();

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
        int totalTime = 0;
        
        for (int i=0; i<rawList.size(); i++) {
            wheelRpm = ( (rawList.get(i).getWheelCount() * 60 ) / rawList.get(i).getTime() * totalImpulse);
            if (bike.isTwoStroke()) {
                engRpm = rawList.get(i).getEngCount() /rawList.get(i).getTime();
            } else {
                engRpm = (rawList.get(i).getEngCount() * 2 ) /rawList.get(i).getTime();
            }
            power.addER(engRpm);
            power.addWR(wheelRpm);
            power.addDTime(rawList.get(i).getTime());
        }
    }
    
    public void calcPower() {
        //Wheel-Power
        for (int i=0; i<power.getWheelRpm().size(); i++) {
            double dOmega = (Math.PI*power.getWheelRpm().get(i))/30;
            double alpha = dOmega/(power.getDTime().get(i)*1000);
            double torque = (config.getInertia()*alpha)/dOmega;
            double power = torque * dOmega; //?????????
        }
    }
    
}
