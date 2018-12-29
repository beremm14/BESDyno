package serial.requests;

import logging.Logger;
import jssc.SerialPortException;
import main.BESDyno;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public abstract class Request {
    
    private static final Logger LOG = Logger.getLogger(Request.class.getName());

    public static enum Status {
        WAITINGTOSEND, WAITINGFORRESPONSE, DONE, ERROR
    };

    protected Status status;

    public Request() {
        status = Status.WAITINGTOSEND;
    }
    
    protected void devLog(String msg) {
        if (BESDyno.getInstance().isDevMode()) {
            LOG.debug(msg);
        }
    }

    public abstract void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException;
    
    public abstract String getReqMessage();
    public abstract String getReqName();

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public abstract void handleResponse(String res);

}
