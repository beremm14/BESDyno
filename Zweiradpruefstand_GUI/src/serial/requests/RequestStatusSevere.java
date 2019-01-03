package serial.requests;

import development.CommunicationLogger;
import java.io.UnsupportedEncodingException;
import jssc.SerialPortException;
import logging.Logger;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestStatusSevere extends Request {

    private static final Logger LOG = Logger.getLogger(RequestEngine.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        try {
            port.writeBytes("v".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq("SEVERE: v");

        status = Request.Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {

        COMLOG.addRes(res);

        if (checkCRC(res) && res.equals(":SEVERE>" + getCRC(res) + ';')) {
            status = Status.DONE;
        } else {
            status = Status.ERROR;
        }
    }

    @Override
    public String getReqMessage() {
        return "SEVERE";
    }

    @Override
    public String getReqName() {
        return "SEVERE";
    }

    @Override
    public Variety getVariety() {
        return Variety.SEVERE;
    }

}
