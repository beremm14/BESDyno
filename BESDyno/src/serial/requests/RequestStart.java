package serial.requests;

import data.Environment;
import development.CommunicationLogger;
import development.LoggedRequest;
import development.LoggedResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import logging.Logger;
import jssc.SerialPortException;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestStart extends Request {

    private static final Logger LOG = Logger.getLogger(RequestStart.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();

    private String response;

    @Override
    public void sendRequest(Object port) throws CommunicationException, SerialPortException {
        if (status != Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }

        LOG.debug("Request START will be sent");
        try {
            if (port instanceof jssc.SerialPort) {
                jssc.SerialPort jsscPort = (jssc.SerialPort) port;
                jsscPort.writeBytes("s".getBytes("UTF-8"));
            } else if (port instanceof gnu.io.SerialPort) {
                gnu.io.SerialPort rxtxPort = (gnu.io.SerialPort) port;
                OutputStream os = rxtxPort.getOutputStream();
                os.write('s');
            }
        } catch (Exception ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq(new LoggedRequest("START"));
        LOG.debug("Request START logged");

        status = Status.WAITINGFORRESPONSE;
        LOG.debug("Request START: WAITING-FOR-RESPONSE");
    }

    @Override
    public void handleResponse(String res) {
        LOG.debug("START-Response: " + res);
        response = res;
        COMLOG.addRes(new LoggedResponse(crc.removeCRC(res), crc.getSentCRC(res), crc.calcCRC(res)));
        LOG.debug("START-Response " + res + " logged");

        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        LOG.debug("START-Response: : and ; replaced");

        if (crc.removeCRC(response).equals("BMP-ERROR")) {
            LOG.severe("ERROR at START: BMP180");
            Environment.getInstance().setNormEnable(false);
            status = Status.ERROR;
            return;
        }
        
        // :Temperature#Pressure#Altitude;
        String values[];
        try {
        values = response.split("#");
        values[2] = crc.removeCRC(values[2]);
        LOG.debug("START-Response: Response-String splitted");
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.severe(ex);
            status = Status.ERROR;
            return;
        }

        if (values[0].isEmpty() || values[1].isEmpty() || values[2].isEmpty()) {
            LOG.warning("START Response maybe incomplete");
        }

        Environment.getInstance().setEnvTemp(Double.parseDouble(values[0]));
        Environment.getInstance().setAirPress(Double.parseDouble(values[1]));
        Environment.getInstance().setAltitude(Double.parseDouble(values[2]));
        LOG.debug("START-Response: Values -> Environment");
        LOG.info("envTemp = " + Environment.getInstance().getEnvTempC()
                + " envPress = " + Environment.getInstance().getAirPress()
                + " envAltitude = " + Environment.getInstance().getAltitude());

        if (Environment.getInstance().getAirPress() > 0 && crc.checkCRC(res)) {
            status = Status.DONE;
            Environment.getInstance().setNormEnable(true);
        } else {
            status = Status.ERROR;
            Environment.getInstance().setNormEnable(false);
        }

    }

    @Override
    public String getReqMessage() {
        return "START: Environment";
    }

    @Override
    public String getReqName() {
        return "START";
    }

    @Override
    public String getResponse() {
        return response;
    }

}
