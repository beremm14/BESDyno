package serial.requests;

import logging.Logger;
import java.util.zip.CRC32;
import jssc.SerialPortException;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public abstract class Request {

    private static final Logger LOG = Logger.getLogger(Request.class.getName());

    public static enum Status {
        WAITINGTOSEND, WAITINGFORRESPONSE, DONE, ERROR, TIMEOUT
    };

    protected Status status;
    protected boolean secondTryAllowed;
    private boolean timeOutComp = true;

    public Request() {
        status = Status.WAITINGTOSEND;
    }

    public abstract void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException;

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

    protected boolean checkCRC(String res) {
        CRC32 crc = new CRC32();
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");

        String toCheck[] = response.split(">");

        byte[] b = toCheck[0].trim().getBytes();
        crc.update(b);
        long checksum = crc.getValue();

        return checksum == Long.parseLong(toCheck[1]);
    }

    protected String removeCRC(String s) {
        String rv = null;
        try {
            String[] value = s.split(">");
            rv = value[0].replaceAll(":", "");
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.severe(ex);
        }

        return rv;
    }

    protected long calcCRC(String res) {
        CRC32 crc = new CRC32();
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");

        String toCheck[] = response.split(">");

        byte[] b = toCheck[0].trim().getBytes();
        crc.update(b);
        return crc.getValue();
    }

    protected long getSentCRC(String res) {
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        String value[];
        try {
            value = response.split(">");
            return Long.parseLong(value[1]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.severe(ex);
            return 0;
        }
    }

}
