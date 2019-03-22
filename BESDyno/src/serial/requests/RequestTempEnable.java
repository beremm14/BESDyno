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
public class RequestTempEnable extends Request {
    
    private static final Logger LOG = Logger.getLogger(RequestTempEnable.class.getName());
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
                jsscPort.writeBytes("t".getBytes("UTF-8"));
            } else if (port instanceof gnu.io.SerialPort) {
                gnu.io.SerialPort rxtxPort = (gnu.io.SerialPort) port;
                OutputStream os = rxtxPort.getOutputStream();
                os.write('t');
            }
        } catch (Exception ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq(new LoggedRequest("TEMPERATURE ENABLE"));

        status = Request.Status.WAITINGFORRESPONSE;
    }
    
    @Override
    public void handleResponse(String res) {
        response = res;
        COMLOG.addRes(new LoggedResponse(removeCRC(res), getSentCRC(res), calcCRC(res)));

        if (checkCRC(res) && res.equals(":TE>" + calcCRC(res) + ';')) {
            status = Status.DONE;
        } else {
            status = Status.ERROR;
        }
    }

    @Override
    public String getReqMessage() {
        return "TEMPERATURE ENABLE";
    }

    @Override
    public String getReqName() {
        return "TEMPERATURE ENABLE";
    }

    @Override
    public String getResponse() {
        return response;
    }
    
}
