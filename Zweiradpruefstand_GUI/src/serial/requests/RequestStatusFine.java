package serial.requests;

import development.CommunicationLogger;
import jssc.SerialPortException;
import logging.Logger;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestStatusFine extends Request {
    
    private static final Logger LOG = Logger.getLogger(RequestEngine.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        port.writeByte((byte) 'f');
        if(COMLOG.isEnabled()) {
            COMLOG.addReq("FINE: f");
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
        return "FINE";
    }

    @Override
    public String getErrorMessage() {
        return "ERROR at FINE";
    }

    @Override
    public String getReqName() {
        return "FINE";
    }
    
}
