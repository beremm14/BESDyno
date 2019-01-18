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
public class RequestMeasure extends Request {

    private static final Logger LOG = Logger.getLogger(RequestEngine.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();

    private String response;

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        try {
            port.writeBytes("m".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq(new LoggedRequest("MEASURE"));

        status = Request.Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {
        synchronized (Database.getInstance().getRawList()) {
            response = res;
            COMLOG.addRes(new LoggedResponse(removeCRC(res), getSentCRC(res), calcCRC(res)));

            String response = res.replaceAll(":", "");
            response = response.replaceAll(";", "");

            // :engCount#rearCount#Time>crc;
            String values[] = response.split("#");
            values[2] = removeCRC(values[2]);

            RawDatapoint dp = new RawDatapoint(values[0], values[1], values[2]);
            if (dp.getTime() > 0) {
                Database.getInstance().addRawDP(dp);
                LOG.debug("MEASURE: engCount: " + dp.getEngCount() + " wheelCount: " + dp.getWheelCount() + " time: " + dp.getTime());
            } else {
                LOG.warning("MEASURE: Time = 0");
            }

            Database.getInstance().getRawList().notifyAll();

            if (checkCRC(res) && dp.getTime() > 0) {
                status = Status.DONE;
            } else {
                status = Status.ERROR;
            }
        }
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
    public Variety getVariety() {
        return Variety.MEASURE;
    }

    @Override
    public String getResponse() {
        return response;
    }

}
