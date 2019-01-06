package data;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author emil
 */
public class BikePower {
    
    private static BikePower instance = null;

    public static BikePower getInstance() {
        if (instance == null) {
            instance = new BikePower();
        }
        return instance;
    }

    //Power
    private final List<Double> engPower = new LinkedList<>();
    private final List<Double> wheelPower = new LinkedList<>();
    
    //RPM
    private final List<Integer> engRpm = new LinkedList<>();
    private final List<Integer> wheelRpm = new LinkedList<>();
    
    //Velocity
    private final List<Double> velList = new LinkedList<>();
    
    //Uncalculated List
    private final List<RawDatapoint> rawList = new LinkedList<>();

    //"Absolute" Power
    private double bikePower;

    private BikePower() {
    }

    //Getter
    public List<Double> getEngPower() {
        return engPower;
    }

    public List<Double> getWheelPower() {
        return wheelPower;
    }

    public List<Integer> getEngRpm() {
        return engRpm;
    }

    public List<Integer> getWheelRpm() {
        return wheelRpm;
    }
    
    public List<RawDatapoint> getRawList() {
        return rawList;
    }
    
    public List<Double> getVelList() {
        return velList;
    }

    public double getBikePower() {
        return bikePower;
    }

    //Setter
    public void setBikePower(double bikePower) {
        this.bikePower = bikePower;
    }

    public void setBikePower() {
        double power = engPower.get(0);
        for (int i = 0; i < engPower.size(); i++) {
            if (engPower.get(i) > power) {
                power = engPower.get(i);
            }
        }
        this.bikePower = power;
    }

    //LinkedList-Methods
    public boolean addEP(Double p) {
        return engPower.add(p);
    }

    public boolean addWP(Double p) {
        return wheelPower.add(p);
    }
    
    public boolean addER(Integer n) {
        return engRpm.add(n);
    }
    
    public boolean addWR(Integer n) {
        return wheelRpm.add(n);
    }
    
    public boolean addRawDP(RawDatapoint dp) {
        return rawList.add(dp);
    }
    
    public boolean addVel(Double v) {
        return velList.add(v);
    }

}
