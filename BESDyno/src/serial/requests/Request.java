package serial.requests;

import logging.Logger;
import java.util.zip.CRC32;
import jssc.SerialPortException;
import serial.CRC;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public abstract class Request {

    private static final Logger LOG = Logger.getLogger(Request.class.getName());
    protected final CRC crc = new CRC();

    public static enum Status {
        WAITINGTOSEND, WAITINGFORRESPONSE, DONE, ERROR, TIMEOUT
    };

    protected Status status;
    protected boolean secondTryAllowed;
    private boolean timeOutComp = true;

    public Request() {
        status = Status.WAITINGTOSEND;
    }

    public abstract void sendRequest(Object port) throws CommunicationException, SerialPortException;

    public abstract String getReqMessage();

    public abstract String getReqName();

    public abstract void handleResponse(String res);

    public abstract String getResponse();

    public Status getStatus() {
        return status;
    }

    public boolean timeOutIsComp() {
        return timeOutComp;
    }

    public boolean secondTryAllowed() {
        return secondTryAllowed;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setSecondTryAllowed(boolean secondTryAllowed) {
        this.secondTryAllowed = secondTryAllowed;
    }

    public void setTimeOutComp(boolean timeOutComp) {
        this.timeOutComp = timeOutComp;
    }

}
