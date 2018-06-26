package data;

/**
 *
 * @author emil
 */
public class RawDatapoint {
    
    private int engCount;   //Counts/time
    private int wheelCount; //Counts/time
    private int time;       //ms

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

    public int getEngCount() {
        return engCount;
    }

    public int getWheelCount() {
        return wheelCount;
    }

    public int getTime() {
        return time;
    }

    public void setEngCount(int engCount) {
        this.engCount = engCount;
    }

    public void setWheelCount(int wheelCount) {
        this.wheelCount = wheelCount;
    }

    public void setTime(int time) {
        this.time = time;
    }

}
