package measure;

import data.Bike;
import data.Config;
import data.Datapoint;
import data.RawDatapoint;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author emil
 */
public class Calculate {
    
    private List<RawDatapoint> rawList;
    
    private Bike bike = new Bike();

    public Calculate(List<RawDatapoint> rawList) {
        this.rawList = rawList;
    }
    
    public List<Datapoint> calcRpm() {
        List<Datapoint> bikeList = new LinkedList<>();
        
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
            totalTime+= rawList.get(i).getTime();
            
            bikeList.add(new Datapoint(engRpm, wheelRpm, totalTime));
        }
        return bikeList;
    }
    
}
