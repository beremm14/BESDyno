package data;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author emil
 */
public class BikePower {
    
    //Power
    private List<Double> engPower = new LinkedList<>();
    private List<Double> wheelPower = new LinkedList<>();
    
    //RPM
    private List<Integer> engRpm = new LinkedList<>();
    private List<Integer> wheelRpm = new LinkedList<>();
    
    //"Absolute" Power
    private double bikePower;

    public BikePower() {}

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

    public double getBikePower() {
        return bikePower;
    }

    //Setter
    public void setBikePower(double bikePower) {
        this.bikePower = bikePower;
    }
    
    public void setBikePower() {
        double power = engPower.get(0);
        for (int i=0; i<engPower.size(); i++) {
            if (engPower.get(i) > power) {
                power = engPower.get(i);
            }
        }
        this.bikePower = power;
    }

    //LinkedList-Methods
    public boolean addEP(Double e) {
        return engPower.add(e);
    }
    
    public boolean addWP(Double e) {
        return wheelPower.add(e);
    }
    
    public boolean addER(Integer e) {
        return engRpm.add(e);
    }
    
    public boolean addWR(Integer e) {
        return wheelRpm.add(e);
    }
    
}
