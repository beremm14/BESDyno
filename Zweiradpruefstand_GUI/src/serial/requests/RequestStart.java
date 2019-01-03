package serial.requests;

import data.Environment;
import development.CommunicationLogger;
import development.LoggedRequest;
import development.LoggedResponse;
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

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }

        LOG.debug("Request START will be sent");
        try {
            port.writeBytes("s".getBytes("UTF-8"));
            LOG.debug("Request START sent");
        } catch (UnsupportedEncodingException ex) {
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

        COMLOG.addRes(new LoggedResponse(removeCRC(res), getSentCRC(res), calcCRC(res)));
        LOG.debug("START-Response " + res + " logged");

        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        LOG.debug("START-Response: : and ; replaced");

        // :Temperature#Pressure#Altitude;
        String values[] = response.split("#");
        values[2] = removeCRC(values[2]);
        LOG.debug("START-Response: Response-String splitted");

        if (values[0].isEmpty() || values[1].isEmpty() || values[2].isEmpty()) {
            LOG.warning("START Response maybe incomplete");
        }

        Environment.getInstance().setEnvTemp(Double.parseDouble(values[0]));
        Environment.getInstance().setAirPress(Double.parseDouble(values[1]));
        Environment.getInstance().setAltitude(Double.parseDouble(values[2]));
        LOG.debug("START-Response: Values -> Environment");
        LOG.info("envTemp = " + Environment.getInstance().getEnvTemp()
                + " envPress = " + Environment.getInstance().getAirPress()
                + " envAltitude = " + Environment.getInstance().getAltitude());

        if (Environment.getInstance().getAirPress() > 0 && checkCRC(res)) {
            status = Status.DONE;
        } else {
            status = Status.ERROR;
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
    public Variety getVariety() {
        return Variety.START;
    }

}
