package serial.requests;

import development.CommunicationLogger;
import jssc.SerialPortException;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestInit extends Request {

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
