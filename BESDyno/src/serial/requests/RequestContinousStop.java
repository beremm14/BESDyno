package serial.requests;

import development.CommunicationLogger;
import development.LoggedRequest;
import development.LoggedResponse;
import java.io.OutputStream;
import jssc.SerialPortException;
import logging.Logger;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestContinousStop extends Request {
    
    private static final Logger LOG = Logger.getLogger(RequestContinousStop.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();
    
    private String response;

    @Override
    public void sendRequest(Object port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        try {
            if (port instanceof jssc.SerialPort) {
                jssc.SerialPort jsscPort = (jssc.SerialPort) port;
                jsscPort.writeBytes("z".getBytes("UTF-8"));
            } else if (port instanceof gnu.io.SerialPort) {
                gnu.io.SerialPort rxtxPort = (gnu.io.SerialPort) port;
                OutputStream os = rxtxPort.getOutputStream();
                os.write('z');
            }
        } catch (Exception ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq(new LoggedRequest("CONTINOUS STOP"));

        status = Request.Status.WAITINGFORRESPONSE;
    }
    
    @Override
    public void handleResponse(String res) {
        response = res;
        COMLOG.addRes(new LoggedResponse(removeCRC(res), getSentCRC(res), calcCRC(res)));

        if (checkCRC(res) && res.equals(":CT>" + calcCRC(res) + ';')) {
            status = Status.DONE;
        } else {
            status = Status.ERROR;
        }
    }

    @Override
    public String getReqMessage() {
        return "CONTINOUS STOP";
    }

    @Override
    public String getReqName() {
        return "CONTINOUS STOP";
    }

    @Override
    public String getResponse() {
        return response;
    }
    
}
