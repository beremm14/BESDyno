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
public class RequestStatusWarning extends Request {
    
    private static final Logger LOG = Logger.getLogger(RequestEngine.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        try {
            port.writeBytes("w".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            LOG.severe(ex);
        }
        if(COMLOG.isEnabled()) {
            COMLOG.addReq("WARNING: w");
        }
        status = Request.Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {
        if(COMLOG.isEnabled()) {
            COMLOG.addRes(res);
        }
    }

    @Override
    public String getReqMessage() {
        return "WARNING";
    }

    @Override
    public String getErrorMessage() {
        return "ERROR at WARNING";
    }

    @Override
    public String getReqName() {
        return "WARNING";
    }
    
}
