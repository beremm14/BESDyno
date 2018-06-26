package data;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author emil
 */
public class Datapoint {
    
    private double engRpm;   //U/min
    private double wheelRpm; //U/min
    private double time;     //ms

    public Datapoint(double engRpm, double wheelRpm, double time) {
        this.engRpm = engRpm;
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

    public void setEngRpm(double engRpm) {
        this.engRpm = engRpm;
    }

    public void setWheelRpm(double wheelRpm) {
        this.wheelRpm = wheelRpm;
    }

    public void setTime(double time) {
        this.time = time;
    }


    
    public void writeLine(BufferedWriter w) throws IOException {
        w.write(time + "");
        w.write("\t");
        w.write(engRpm + "");
        w.write("\t");
        w.write(wheelRpm + "");
    }
    
}
