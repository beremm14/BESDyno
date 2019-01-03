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
public class RequestEngine extends Request {

    private static final Logger LOG = Logger.getLogger(RequestEngine.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        port.writeByte((byte) 'e');
        COMLOG.addReq("ENGINE: e");

        status = Request.Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {

        COMLOG.addRes(res);

        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");

        // :EngineTemp#FumeTemp;
        String values[] = response.split("#");
        values[1] = removeCRC(values[1]);
        if (values[0].isEmpty() || values[1].isEmpty()) {
            LOG.warning("START Response maybe incomplete");
        }
        Environment.getInstance().setEngTemp(Double.parseDouble(values[0]));
        Environment.getInstance().setFumeTemp(Double.parseDouble(values[1]));
        LOG.info("engTemp = " + Environment.getInstance().getEngTemp()
                + " fumeTemp = " + Environment.getInstance().getFumeTemp());
        
        if ((Environment.getInstance().getEngTemp() <= 0 || Environment.getInstance().getFumeTemp() <= 0)
             && checkCRC(res)) {
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
    public Variety getVariety() {
        return Variety.ENGINE;
    }

}
