package serial.requests;

import data.Environment;
import development.CommunicationLogger;
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
        port.writeByte((byte) 's');
        if(COMLOG.isEnabled()) {
            COMLOG.addReq("START: s");
        }
        status = Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {
        if(COMLOG.isEnabled()) {
            COMLOG.addRes(res);
        }
        
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        
        // :Temperature#Pressure#Altitude;
        String values[] = response.split("#");
        if(values[0].isEmpty() || values[1].isEmpty() || values[2].isEmpty()) {
            LOG.warning("START Response maybe incomplete");
        }
        Environment.getInstance().setEnvTemp(Double.parseDouble(values[0]));
        Environment.getInstance().setAirPress(Double.parseDouble(values[1]));
        Environment.getInstance().setAltitude(Double.parseDouble(values[2]));
        LOG.info(    "envTemp = " + Environment.getInstance().getEnvTemp() +
                   " envPress = " + Environment.getInstance().getAirPress() +
                " envAltitude = " + Environment.getInstance().getAltitude());
    }

    @Override
    public String getReqName() {
        return "START: Environment";
    }

    @Override
    public String getErrorMessage() {
        return "ERROR at START";
    }
    
}
