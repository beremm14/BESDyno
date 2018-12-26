package serial.requests;

import development.CommunicationLogger;
import logging.Logger;
import jssc.SerialPortException;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestReset extends Request {

    private static final Logger LOG = Logger.getLogger(RequestReset.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        port.writeByte((byte) 'r');
        if(COMLOG.isEnabled()) {
            COMLOG.addReq("RESET: r");
        }
        status = Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {
        if(COMLOG.isEnabled()) {
            COMLOG.addRes(res);
        }
        
        if(res != ":RESET;") {
            status = Status.ERROR;
        } else {
            status = Status.DONE;
        }
        
    }

    @Override
    public String getReqName() {
        return "RESET";
    }

    @Override
    public String getErrorMessage() {
        return "ERROR at RESET";
    }
 
}
