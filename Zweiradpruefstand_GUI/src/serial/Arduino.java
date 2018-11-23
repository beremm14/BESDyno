package serial;

import java.io.UnsupportedEncodingException;
import logging.Logger;
import jssc.SerialPortException;

/**
 *
 * @author emil
 */
public class Arduino {

    private static Arduino instance = null;
    private static final Logger LOG = Logger.getLogger(Arduino.class.getName());
    
    

    public static enum Request {
        INIT, START, ENGINE, MEASURE, MEASURENO, RESET;
    }

    public static Arduino getInstance() throws SerialPortException {
        if (instance == null) {
            instance = new Arduino();
        }
        return instance;
    }
    
    private String response;

    private Arduino() {
    }

    public void sendRequest(Request request) throws UnsupportedEncodingException, SerialPortException, Exception {
        switch (request) {
            case INIT:
                Port.getInstance().getPort().writeByte((byte) 'i');
                break;
            case START:
                Port.getInstance().getPort().writeByte((byte) 's');
                break;
            case ENGINE:
                Port.getInstance().getPort().writeBytes("e".getBytes("UTF-8"));
                break;
            case MEASURE:
                Port.getInstance().getPort().writeBytes("m".getBytes("UTF-8"));
                break;
            case MEASURENO:
                Port.getInstance().getPort().writeBytes("n".getBytes("UTF-8"));
                break;
            case RESET:
                Port.getInstance().getPort().writeBytes("r".getBytes("UTF-8"));
                break;
            default:
                throw new CommunicationException("Communication Problem...");
        }
    }

}
