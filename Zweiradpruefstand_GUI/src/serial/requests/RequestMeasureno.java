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
public class RequestMeasureno extends Request {

    private static final Logger LOG = Logger.getLogger(RequestEngine.class.getName());
    private static final CommunicationLogger COMLOG = CommunicationLogger.getInstance();

    private String response;

    @Override
    public void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        try {
            port.writeBytes("n".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq(new LoggedRequest("MEASURENO"));

        status = Request.Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {
        synchronized (Database.getInstance().syncObj) {
            response = res;
            COMLOG.addRes(new LoggedResponse(removeCRC(res), getSentCRC(res), calcCRC(res)));

            String response = res.replaceAll(":", "");
            response = response.replaceAll(";", "");

            // :rearCount#Time>crc;
            String values[] = response.split("#");
            values[1] = removeCRC(values[1]);

            RawDatapoint dp = new RawDatapoint(values[0], values[1]);
            if (dp.getTime() > 0) {
                Database.getInstance().addRawDP(dp);
                LOG.debug("MEASURENO: wheelCount: " + dp.getWheelCount() + " time: " + dp.getTime());
            } else {
                LOG.warning("MEASURENO: Time = 0");
            }

            Database.getInstance().syncObj.notifyAll();
            LOG.debug("Measurement-syncObj notified");

            if (checkCRC(res) && dp.getTime() > 0) {
                status = Status.DONE;
            } else {
                status = Status.ERROR;
            }
        }
    }

    @Override
    public String getReqMessage() {
        return "MEASURENO: Wheel only";
    }

    @Override
    public String getReqName() {
        return "MEASURENO";
    }

    @Override
    public String getResponse() {
        return response;
    }

}
