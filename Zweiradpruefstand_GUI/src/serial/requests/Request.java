package serial.requests;

import jssc.SerialPortException;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public abstract class Request {
    
    public static enum Status {
        WAITINGTOSEND, WAITINGFORRESPONSE, DONE, ERROR
    };
    
    public static enum Variety {
        INIT, START, MEASURE, MEASURENO, ENGINE, FINE, WARNING, SEVERE, MAXPROBLEMS
    };

    protected Status status;

    public Request() {
        status = Status.WAITINGTOSEND;
    }
    
    public abstract void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException;
    
    public abstract String getReqMessage();
    public abstract String getReqName();
    public abstract Variety getVariety();

    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }

    public abstract void handleResponse(String res);

}
