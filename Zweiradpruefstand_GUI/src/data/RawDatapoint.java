package data;

/**
 *
 * @author emil
 */
public class RawDatapoint {
    
    private final int engCount;   //Counts/time
    private final int wheelCount; //Counts/time
    private final int time;       //ms (dt)

    public RawDatapoint(String engCount, String wheelCount, String time) {
        this.engCount = Integer.parseInt(engCount);
        this.wheelCount = Integer.parseInt(wheelCount);
        this.time = Integer.parseInt(time);
    }

    public RawDatapoint(int engCount, int wheelCount, int time) {
        this.engCount = engCount;
        this.wheelCount = wheelCount;
        this.time = time;
    }
    
    public RawDatapoint(String wheelCount, String time) {
        this.engCount = 0;
        this.wheelCount = Integer.parseInt(wheelCount);
        this.time = Integer.parseInt(time);
    }
    
    public RawDatapoint(int wheelCount, int time) {
        this.engCount = 0;
        this.wheelCount = wheelCount;
        this.time = time;
    }

    public int getEngCount() {
        return engCount;
    }

    public int getWheelCount() {
        return wheelCount;
    }

    public int getTime() {
        return time;
    }

}
