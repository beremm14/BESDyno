package serial.requests;

import development.CommunicationLogger;
import development.LoggedRequest;
import development.LoggedResponse;
import java.io.UnsupportedEncodingException;
import logging.Logger;
import jssc.SerialPortException;
import main.BESDyno;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestInit extends Request {

    private static final Logger LOG = Logger.getLogger(RequestInit.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();
    
    private String response;

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }

        LOG.debug("Request INIT will be sent");
        try {
            Thread.sleep(2000);
            port.writeBytes("i".getBytes("UTF-8"));
            BESDyno.getInstance().userLog("Initialisierungs-Anfrage wurde an das Gerät gesendet", BESDyno.LogLevel.INFO);
            LOG.debug("Request INIT sent");
        } catch (UnsupportedEncodingException | InterruptedException ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq(new LoggedRequest("INIT"));
        LOG.debug("Request INIT logged");

        status = Request.Status.WAITINGFORRESPONSE;
        LOG.debug("Request INIT: WAITING-FOR-RESPONSE");
    }

    @Override
    public void handleResponse(String res) {
        response = res;
        LOG.debug("INIT-Response: " + res);
        
        COMLOG.addRes(new LoggedResponse(removeCRC(res), getSentCRC(res), calcCRC(res)));
        LOG.debug("INIT-Response " + res + " logged");

        if (checkCRC(res) && res.equals(":BESDyno>" + calcCRC(res) + ';')) {
            status = Status.DONE;
        } else {
            status = Status.ERROR;
        }

    }

    @Override
    public String getReqMessage() {
        return "INIT";
    }

    @Override
    public String getReqName() {
        return "INIT";
    }

    @Override
    public String getResponse() {
        return response;
    }

}
