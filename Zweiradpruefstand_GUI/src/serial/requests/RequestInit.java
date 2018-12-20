package serial.requests;

import development.CommunicationLogger;
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
        port.writeByte((byte) 'i');
        if(COMLOG.isEnabled()) {
            COMLOG.addReq("i");
        }
        status = Request.Status.WAITINGFORRESPONSE;
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
    
    
    
}
