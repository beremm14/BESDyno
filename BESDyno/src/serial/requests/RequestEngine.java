package serial.requests;

import data.Environment;
import development.CommunicationLogger;
import development.LoggedRequest;
import development.LoggedResponse;
import java.io.OutputStream;
import logging.Logger;
import jssc.SerialPortException;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestEngine extends Request {

    private static final Logger LOG = Logger.getLogger(RequestEngine.class.getName());
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
                jsscPort.writeBytes("e".getBytes("UTF-8"));
            } else if (port instanceof gnu.io.SerialPort) {
                gnu.io.SerialPort rxtxPort = (gnu.io.SerialPort) port;
                OutputStream os = rxtxPort.getOutputStream();
                os.write('e');
            }
        } catch (Exception ex) {
            LOG.severe(ex);
        }
        COMLOG.addReq(new LoggedRequest("ENGINE"));

        status = Request.Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {
        response = res;
        COMLOG.addRes(new LoggedResponse(crc.removeCRC(res), crc.getSentCRC(res), crc.calcCRC(res)));

        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");

        // :EngineTemp#FumeTemp;
        String values[];
        try {
            values = response.split("#");
            values[1] = crc.removeCRC(values[1]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.severe(ex);
            status = Status.ERROR;
            return;
        }
        if (values[0].isEmpty() || values[1].isEmpty()) {
            LOG.warning("START Response maybe incomplete");
        }
        Environment.getInstance().setEngTemp(Double.parseDouble(values[0]));
        Environment.getInstance().setFumeTemp(Double.parseDouble(values[1]));
        LOG.info("engTemp = " + Environment.getInstance().getEngTempC()
                + " fumeTemp = " + Environment.getInstance().getFumeTempC());

        if ((Environment.getInstance().getEngTempC() <= 0 || Environment.getInstance().getFumeTempC() <= 0)
                && crc.checkCRC(res)) {
            status = Status.ERROR;
        } else {
            status = Status.DONE;
        }

    }

    @Override
    public String getReqMessage() {
        return "ENGINE: Motorcycle Temperatures";
    }

    @Override
    public String getReqName() {
        return "ENGINE";
    }

    @Override
    public String getResponse() {
        return response;
    }

}
