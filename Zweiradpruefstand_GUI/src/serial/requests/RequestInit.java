package serial.requests;

import development.CommunicationLogger;
import logging.Logger;
import jssc.SerialPortException;
import main.BESDyno;
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
        port.writeByte((byte) 'i');
        devLog("Request INIT sent");
        
        if(COMLOG.isEnabled()) {
            COMLOG.addReq("INIT: i");
            devLog("Request INIT logged");
        }
        
        status = Request.Status.WAITINGFORRESPONSE;
        devLog("Request INIT: WAITING-FOR-RESPONSE");
    }

    @Override
    public void handleResponse(String res) {
        if(COMLOG.isEnabled()) {
            COMLOG.addRes(res);
        }
        
        if(res != ":BESDyno;") {
            status = Request.Status.ERROR;
        } else {
            status = Request.Status.DONE;
        }
        
    }

    @Override
    public String getReqName() {
        return "INIT";
    }

    @Override
    public String getErrorMessage() {
        return "ERROR at INIT";
    }

}
