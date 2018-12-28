package serial.requests;

import data.Environment;
import development.CommunicationLogger;
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
        
        devLog("Request START will be sent");
        try {
            port.writeBytes("s".getBytes("UTF-8"));
            devLog("Request START sent");
        } catch (UnsupportedEncodingException ex) {
            LOG.severe(ex);
        }
        
        if(COMLOG.isEnabled()) {
            COMLOG.addReq("START: s");
            devLog("Request START logged");
        }
        
        status = Status.WAITINGFORRESPONSE;
        devLog("Request START: WAITING-FOR-RESPONSE");
    }

    @Override
    public void handleResponse(String res) {
        devLog("START-Response: " + res);
        if(COMLOG.isEnabled()) {
            COMLOG.addRes(res);
            devLog("START-Response " + res + " logged");
        }
        
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        devLog("START-Response: : and ; replaced");
        
        // :Temperature#Pressure#Altitude;
        String values[] = response.split("#");
        devLog("START-Response: Response-String splitted");
        
        if(values[0].isEmpty() || values[1].isEmpty() || values[2].isEmpty()) {
            LOG.warning("START Response maybe incomplete");
        }
        
        Environment.getInstance().setEnvTemp(Double.parseDouble(values[0]));
        Environment.getInstance().setAirPress(Double.parseDouble(values[1]));
        Environment.getInstance().setAltitude(Double.parseDouble(values[2]));
        devLog("START-Response: Values -> Environment");
        LOG.info(    "envTemp = " + Environment.getInstance().getEnvTemp() +
                   " envPress = " + Environment.getInstance().getAirPress() +
                " envAltitude = " + Environment.getInstance().getAltitude());
    }

    @Override
    public String getReqMessage() {
        return "START: Environment";
    }

    @Override
    public String getErrorMessage() {
        return "ERROR at START";
    }

    @Override
    public String getReqName() {
        return "START";
    }
    
}
