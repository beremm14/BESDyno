package measure;

/**
 *
 * @author emil
 */
public class Power {
    
    private double engPower;
    private double wheelPower;
    private int engRpm;
    private int wheelRpm;

    //Getter
    public double getEngPower() {
        return engPower;
    }

    public double getWheelPower() {
        return wheelPower;
    }

    public int getEngRpm() {
        return engRpm;
    }

    public int getWheelRpm() {
        return wheelRpm;
    }

    //Setter
    public void setEngPower(double engPower) {
        this.engPower = engPower;
    }

    public void setWheelPower(double wheelPower) {
        this.wheelPower = wheelPower;
    }

    public void setEngRpm(int engRpm) {
        this.engRpm = engRpm;
    }

    public void setWheelRpm(int wheelRpm) {
        this.wheelRpm = wheelRpm;
    }
    
}
