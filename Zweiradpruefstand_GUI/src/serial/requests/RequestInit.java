package serial.requests;

import development.CommunicationLogger;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import logging.Logger;
import jssc.SerialPortException;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestInit extends Request {

    private static final Logger LOG = Logger.getLogger(RequestInit.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }

        LOG.debug("Request INIT will be sent");
        try {
            port.writeBytes("i".getBytes("UTF-8"));
            LOG.debug("Request INIT sent");
        } catch (UnsupportedEncodingException ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq("INIT: i");
        LOG.debug("Request INIT logged");

        status = Request.Status.WAITINGFORRESPONSE;
        LOG.debug("Request INIT: WAITING-FOR-RESPONSE");
    }

    @Override
    public void handleResponse(String res) {
        LOG.debug("INIT-Response: " + res);

        COMLOG.addRes(res);
        LOG.debug("INIT-Response " + res + " logged");

        if (!res.equals(":BESDyno;")) {
            status = Request.Status.ERROR;
        } else {
            status = Request.Status.DONE;
        }

    }

    @Override
    public String getReqMessage() {
        return "INIT";
    }

    @Override
    public String getReqName() {
        return "INIT";
    }

    @Override
    public Variety getVariety() {
        return Variety.INIT;
    }

}
