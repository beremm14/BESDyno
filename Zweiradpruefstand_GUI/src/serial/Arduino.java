package serial;

import java.io.UnsupportedEncodingException;
import jssc.SerialPortException;

/**
 *
 * @author emil
 */
public class Arduino {

    private static Arduino instance = null;

    public static enum Request {
        START, ENGINE, MEASURE, MEASURENO, RESET;
    }
    
    public static Arduino getInstance() {
        if (instance == null) {
            instance = new Arduino();
        }
        return instance;
    }

    
    private Arduino() {
    }

    public void sendRequest(Request request) throws UnsupportedEncodingException, SerialPortException, Exception {
        switch (request) {
            case START:
                Port.getInstance().getPort().writeByte((byte)'s');
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

    public String receiveResponse() throws SerialPortException {
        return Port.getInstance().getPort().readString().trim();
    }
}
