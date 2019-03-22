package serial.requests;

import data.Config;
import development.CommunicationLogger;
import development.LoggedRequest;
import development.LoggedResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
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
    public void sendRequest(Object port) throws CommunicationException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        try {
            if (port instanceof jssc.SerialPort) {
                jssc.SerialPort jsscPort = (jssc.SerialPort) port;
                jsscPort.writeBytes("p".getBytes("UTF-8"));
            } else if (port instanceof gnu.io.SerialPort) {
                gnu.io.SerialPort rxtxPort = (gnu.io.SerialPort) port;
                OutputStream os = rxtxPort.getOutputStream();
                os.write('p');
            }
        } catch (Exception ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq(new LoggedRequest("VERSION"));

        status = Request.Status.WAITINGFORRESPONSE;
    }
    
    @Override
    public void handleResponse(String res) {
        response = res;
        COMLOG.addRes(new LoggedResponse(crc.removeCRC(res), crc.getSentCRC(res), crc.calcCRC(res)));
        
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        Config.getInstance().setArduinoVersion(Double.parseDouble(crc.removeCRC(response)));

        if (crc.checkCRC(res) && Config.getInstance().getArduinoVersion() >= BESDyno.getInstance().getReqArduVers()) {
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
    public String getResponse() {
        return response;
    }

}
