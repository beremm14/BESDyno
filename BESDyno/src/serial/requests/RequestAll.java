package serial.requests;

import data.Database;
import data.RawDatapoint;
import development.CommunicationLogger;
import development.LoggedRequest;
import development.LoggedResponse;
import java.io.UnsupportedEncodingException;
import jssc.SerialPortException;
import logging.Logger;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public class RequestAll extends Request {

    private static final Logger LOG = Logger.getLogger(RequestEngine.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();

    private String response;

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        try {
            port.writeBytes("a".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq(new LoggedRequest("ALL"));

        status = Request.Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {

        response = res;
        COMLOG.addRes(new LoggedResponse(removeCRC(res), getSentCRC(res), calcCRC(res)));

        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");

        // :engTime#rearTime#engTemp#fumeTemp#Time>crc;
        String values[];
        try {
            values = response.split("#");
            values[4] = removeCRC(values[4]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.severe(ex);
            status = Status.ERROR;
            return;
        }

        RawDatapoint rdp;
        synchronized (Database.getInstance().getRawList()) {
            rdp = new RawDatapoint(values[0], values[1], values[4]);
            if (rdp.getTime() > 0) {
                Database.getInstance().addRawDP(rdp);
                Database.getInstance().addTemperatures(values[2], values[3]);
            } else {
                LOG.warning("MEASURE: Time = 0");
            }
        }

        if (checkCRC(res) && rdp.getTime() > 0) {
            status = Request.Status.DONE;
        } else {
            status = Request.Status.ERROR;
        }

        LOG.info("--> engTime: " + values[0] + " wheelTime: " + values[1] + " engTemp: " + values[2] + " exhTemp: " + values[3] + " time: " + values[4] + " crc: " + calcCRC(res));

    }

    @Override
    public String getReqMessage() {
        return "MEASURE: Engine & Wheel";
    }

    @Override
    public String getReqName() {
        return "MEASURE";
    }

    @Override
    public String getResponse() {
        return response;
    }

}
