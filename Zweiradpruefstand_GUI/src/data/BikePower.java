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
    private List<Double> engPower = new LinkedList<>();
    private List<Double> wheelPower = new LinkedList<>();

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
    public boolean addEP(Double e) {
        return engPower.add(e);
    }

    public boolean addWP(Double e) {
        return wheelPower.add(e);
    }

}
