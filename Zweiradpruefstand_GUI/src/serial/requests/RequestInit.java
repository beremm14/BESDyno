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
        
        devLog("Request INIT will be sent");
        //port.writeString("i");
        try {
            port.writeBytes("i".getBytes("UTF-8"));
            devLog("Request INIT sent");
        } catch (UnsupportedEncodingException ex) {
            LOG.severe(ex);
        }
        
        
        if(COMLOG.isEnabled()) {
            COMLOG.addReq("INIT: i");
            devLog("Request INIT logged");
        }
        
        status = Request.Status.WAITINGFORRESPONSE;
        devLog("Request INIT: WAITING-FOR-RESPONSE");
    }

    @Override
    public void handleResponse(String res) {
        devLog("INIT-Response: " + res);
        if(COMLOG.isEnabled()) {
            COMLOG.addRes(res);
            devLog("INIT-Response " + res + " logged");
        }
        
        if(!res.equals(":BESDyno;")) {
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
    public String getErrorMessage() {
        return "ERROR at INIT";
    }

    @Override
    public String getReqName() {
        return "INIT";
    }

}
