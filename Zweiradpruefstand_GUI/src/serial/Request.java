package serial;

import java.io.UnsupportedEncodingException;
import logging.Logger;
import jssc.SerialPortException;

/**
 *
 * @author emil
 */
public class Request {

    private static final Logger LOG = Logger.getLogger(Request.class.getName());
    
    

    public static enum Requests {
        INIT, START, ENGINE, MEASURE, MEASURENO, RESET;
    }
    

    public void sendRequest(Requests request) throws UnsupportedEncodingException, SerialPortException, Exception {
        switch (request) {
            case INIT:
                Port.getInstance().getPort().writeByte((byte) 'i');
                LOG.info("Request INIT sent");
                break;
            case START:
                Port.getInstance().getPort().writeByte((byte) 's');
                LOG.info("Request START sent");
                break;
            case ENGINE:
                Port.getInstance().getPort().writeBytes("e".getBytes("UTF-8"));
                LOG.info("Request ENGINE sent");
                break;
            case MEASURE:
                Port.getInstance().getPort().writeBytes("m".getBytes("UTF-8"));
                LOG.info("Request MEASURE sent");
                break;
            case MEASURENO:
                Port.getInstance().getPort().writeBytes("n".getBytes("UTF-8"));
                LOG.info("Request MEASURENO sent");
                break;
            case RESET:
                Port.getInstance().getPort().writeBytes("r".getBytes("UTF-8"));
                LOG.info("Request RESET sent");
                break;
            default:
                throw new CommunicationException("Communication Problem...");
        }
    }

}
