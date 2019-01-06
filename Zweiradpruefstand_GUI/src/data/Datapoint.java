package data;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author emil
 */
public class Datapoint {
    
    private int engRpm;   //U/min
    private int wheelRpm; //U/min
    private int time;     //ms

    public Datapoint(int engRpm, int wheelRpm, int time) {
        this.engRpm = engRpm;
        this.wheelRpm = wheelRpm;
        this.time = time;
    }
    
    public Datapoint(int wheelRpm, int time) {
        this.engRpm = 0;
        this.wheelRpm = wheelRpm;
        this.time = time;
    }

    public int getEngRpm() {
        return engRpm;
    }

    public int getWheelRpm() {
        return wheelRpm;
    }

    public int getTime() {
        return time;
    }

    public void setEngRpm(int engRpm) {
        this.engRpm = engRpm;
    }

    public void setWheelRpm(int wheelRpm) {
        this.wheelRpm = wheelRpm;
    }

    public void setTime(int time) {
        this.time = time;
    }


    
    public void writeLine(BufferedWriter w) throws IOException {
        w.write(String.format("%d", time));
        w.write("\t");
        w.write(String.format("%d", engRpm));
        w.write("\t");
        w.write(String.format("%d", wheelRpm));
    }
    
}
