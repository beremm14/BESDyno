package serial;

/**
 *
 * @author emil
 */
public class Response {
    
    private final StringBuilder receivedFrame = new StringBuilder(1024);
    private long startTime;
    private ResponseStatus returnValue;
    
    public enum ResponseStatus {
        FINISHED, TIMEOUT, ERROR
    };

    public Response() {
        this.startTime = System.currentTimeMillis();
    }

    public StringBuilder getReceivedFrame() {
        return receivedFrame;
    }

    public long getStartTime() {
        return startTime;
    }

    public ResponseStatus getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(ResponseStatus returnValue) {
        this.returnValue = returnValue;
    }
    
    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
    }

}
