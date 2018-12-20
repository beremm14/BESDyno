package serial.requests;

import jssc.SerialPortException;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestReset extends Request {
    
    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        port.writeByte((byte) 'r');
        status = Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {
        if(res != ":RESET;") {
            status = Status.ERROR;
        } else {
            status = Status.DONE;
        }
        
    }
    
    
    
}
