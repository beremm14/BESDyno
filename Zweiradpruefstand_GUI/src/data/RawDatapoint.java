package data;

/**
 *
 * @author emil
 */
public class RawDatapoint {
    
    private final int engTime;   //µs (edge to edge)
    private final int wheelTime; //µs (edge to edge)
    private final int time;       //µs (dt)

    public RawDatapoint(String engCount, String wheelCount, String time) {
        this.engTime = Integer.parseInt(engCount);
        this.wheelTime = Integer.parseInt(wheelCount);
        this.time = Integer.parseInt(time);
    }

    public RawDatapoint(int engCount, int wheelCount, int time) {
        this.engTime = engCount;
        this.wheelTime = wheelCount;
        this.time = time;
    }
    
    public RawDatapoint(String wheelCount, String time) {
        this.engTime = 0;
        this.wheelTime = Integer.parseInt(wheelCount);
        this.time = Integer.parseInt(time);
    }
    
    public RawDatapoint(int wheelCount, int time) {
        this.engTime = 0;
        this.wheelTime = wheelCount;
        this.time = time;
    }

    public int getEngTime() {
        return engTime;
    }

    public int getWheelTime() {
        return wheelTime;
    }

    public int getTime() {
        return time;
    }

}
