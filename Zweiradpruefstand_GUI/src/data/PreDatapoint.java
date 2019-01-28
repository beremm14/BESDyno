package data;

/**
 *
 * @author emil
 */
public class PreDatapoint {
    
    private final double engRpm;   //U/min
    private final double wheelRpm; //U/min
    private final double time;     //seconds

    public PreDatapoint(double engRpm, double wheelRpm, double time) {
        this.engRpm = engRpm;
        this.wheelRpm = wheelRpm;
        this.time = time;
    }
    
    public PreDatapoint(double wheelRpm, double time) {
        this.engRpm = 0;
        this.wheelRpm = wheelRpm;
        this.time = time;
    }

    public double getEngRpm() {
        return engRpm;
    }

    public double getWheelRpm() {
        return wheelRpm;
    }

    public double getTime() {
        return time;
    }

}
