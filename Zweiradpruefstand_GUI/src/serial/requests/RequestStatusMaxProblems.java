package serial.requests;

import development.CommunicationLogger;
import java.io.UnsupportedEncodingException;
import jssc.SerialPortException;
import logging.Logger;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestStatusMaxProblems extends Request {

    private static final Logger LOG = Logger.getLogger(RequestEngine.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        try {
            port.writeBytes("x".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq("MAXPROBLEMS: x");

        status = Request.Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {

        COMLOG.addRes(res);

        if (res.equals(":MAXPROBLEMS;")) {
            status = Request.Status.DONE;
        } else {
            status = Request.Status.ERROR;
        }
    }

    @Override
    public String getReqMessage() {
        return "MAXPROBLEMS";
    }

    @Override
    public String getReqName() {
        return "MAXPROBLEMS";
    }

    @Override
    public Variety getVariety() {
        return Variety.MAXPROBLEMS;
    }
}
