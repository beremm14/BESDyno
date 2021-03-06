package serial.requests;

import data.Database;
import data.RawDatapoint;
import development.CommunicationLogger;
import development.LoggedRequest;
import development.LoggedResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
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
    public void sendRequest(Object port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        try {
            if (port instanceof jssc.SerialPort) {
                jssc.SerialPort jsscPort = (jssc.SerialPort) port;
                jsscPort.writeBytes("a".getBytes("UTF-8"));
            } else if (port instanceof gnu.io.SerialPort) {
                gnu.io.SerialPort rxtxPort = (gnu.io.SerialPort) port;
                OutputStream os = rxtxPort.getOutputStream();
                os.write('a');
            }
        } catch (Exception ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq(new LoggedRequest("ALL"));

        status = Request.Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {
        
        try {

        response = res;
        COMLOG.addRes(new LoggedResponse(crc.removeCRC(res), crc.getSentCRC(res), crc.calcCRC(res)));

        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");

        // :engTime#rearTime#engTemp#fumeTemp#Time>crc;
        String values[];
        try {
            values = response.split("#");
            values[4] = crc.removeCRC(values[4]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.severe(ex);
            status = Status.ERROR;
            return;
        }

        RawDatapoint rdp;
        synchronized (Database.getInstance().getRawList()) {
            try {
                rdp = new RawDatapoint(values[0], values[1], values[4]);
            } catch (NumberFormatException ex) {
                LOG.severe(ex);
                status = Status.ERROR;
                return;
            }
            if (rdp.getTime() > 0) {
                Database.getInstance().addRawDP(rdp);
                Database.getInstance().addTemperatures(values[2], values[3]);
            } else {
                LOG.warning("MEASURE: Time = 0");
            }
        }

        if (crc.checkCRC(res) && rdp.getTime() > 0) {
            status = Request.Status.DONE;
        } else {
            status = Request.Status.ERROR;
        }

        LOG.info("--> engTime: " + values[0] + " wheelTime: " + values[1] + " engTemp: " + values[2] + " exhTemp: " + values[3] + " time: " + values[4] + " crc: " + crc.calcCRC(res));
        
        } catch (Exception ex) {
            LOG.severe(ex);
            status = Status.ERROR;
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
    public String getResponse() {
        return response;
    }

}
