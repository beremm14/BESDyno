package serial.requests;

import data.Database;
import data.RawDatapoint;
import development.CommunicationLogger;
import development.LoggedRequest;
import development.LoggedResponse;
import java.io.OutputStream;
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
    public void sendRequest(Object port) throws CommunicationException, SerialPortException {
        if (status != Request.Status.WAITINGTOSEND) {
            throw new CommunicationException("Request bereits gesendet");
        }
        try {
            if (port instanceof jssc.SerialPort) {
                jssc.SerialPort jsscPort = (jssc.SerialPort) port;
                jsscPort.writeBytes("n".getBytes("UTF-8"));
            } else if (port instanceof gnu.io.SerialPort) {
                gnu.io.SerialPort rxtxPort = (gnu.io.SerialPort) port;
                OutputStream os = rxtxPort.getOutputStream();
                os.write('n');
            }
        } catch (Exception ex) {
            LOG.severe(ex);
        }

        COMLOG.addReq(new LoggedRequest("MEASURENO"));

        status = Request.Status.WAITINGFORRESPONSE;
    }

    @Override
    public void handleResponse(String res) {
        try {
        
        synchronized (Database.getInstance().syncObj) {
            response = res;
            COMLOG.addRes(new LoggedResponse(removeCRC(res), getSentCRC(res), calcCRC(res)));

            String response = res.replaceAll(":", "");
            response = response.replaceAll(";", "");

            // :rearCount#Time>crc;
            String values[];
            try {
                values = response.split("#");
                values[1] = removeCRC(values[1]);
            } catch (ArrayIndexOutOfBoundsException ex) {
                LOG.severe(ex);
                status = Status.ERROR;
                return;
            }
            RawDatapoint rdp;
            try {
                rdp = new RawDatapoint(values[0], values[1]);
            } catch (NumberFormatException ex) {
                LOG.severe(ex);
                status = Status.ERROR;
                return;
            }
            if (rdp.getTime() > 0) {
                Database.getInstance().addRawDP(rdp);
                LOG.debug("MEASURENO: wheelCount: " + rdp.getWheelTime() + " time: " + rdp.getTime());
            } else {
                LOG.warning("MEASURENO: Time = 0");
            }

            Database.getInstance().syncObj.notifyAll();
            LOG.debug("Measurement-syncObj notified");

            if (checkCRC(res) && rdp.getTime() > 0) {
                status = Status.DONE;
            } else {
                status = Status.ERROR;
            }
        }
        } catch (Exception ex) {
            LOG.severe(ex);
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
