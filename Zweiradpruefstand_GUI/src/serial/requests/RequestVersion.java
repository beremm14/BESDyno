package serial.requests;

import data.Config;
import development.CommunicationLogger;
import development.LoggedRequest;
import development.LoggedResponse;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;
import main.BESDyno;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestVersion extends Request {

    private static final logging.Logger LOG = logging.Logger.getLogger(RequestEngine.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();
    
    private String response;

    @Override
    public void sendRequest(SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        try {
            port.writeBytes("p".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq(new LoggedRequest("VERSION"));

        status = Request.Status.WAITINGFORRESPONSE;
    }
    
    @Override
    public void handleResponse(String res) {
        response = res;
        COMLOG.addRes(new LoggedResponse(removeCRC(res), getSentCRC(res), calcCRC(res)));
        
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        Config.getInstance().setArduinoVersion(Double.parseDouble(removeCRC(response)));

        if (checkCRC(res) && Config.getInstance().getArduinoVersion() >= BESDyno.getInstance().getReqArduVers()) {
            status = Status.DONE;
        } else {
            status = Status.ERROR;
        }
    }

    @Override
    public String getReqMessage() {
        return "VERSION";
    }

    @Override
    public String getReqName() {
        return "VERSION";
    }

    @Override
    public Variety getVariety() {
        return Variety.VERSION;
    }

    @Override
    public String getResponse() {
        return response;
    }
    
}
