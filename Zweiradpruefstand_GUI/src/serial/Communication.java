package serial;

import java.io.UnsupportedEncodingException;
import jssc.SerialPortException;

/**
 *
 * @author emil
 */
public class Communication {

    private static Communication instance = null;

    private Port port = Port.getInstance();

    public static Communication getInstance() {
        if (instance == null) {
            instance = new Communication();
        }
        return instance;
    }

    private Communication() {
    }
    
    public enum Request {
        START, ENGINE, MEASURE, MEASURENO, RESET;
    }

    public void sendRequest(Request request) throws UnsupportedEncodingException, SerialPortException, Exception {
        switch(request) {
            case START:
                port.getPort().writeBytes("s".getBytes("UTF-8"));
                break;
            case ENGINE:
                port.getPort().writeBytes("e".getBytes("UTF-8"));
                break;
            case MEASURE:
                port.getPort().writeBytes("m".getBytes("UTF-8"));
                break;
            case MEASURENO:
                port.getPort().writeBytes("n".getBytes("UTF-8"));
                break;
            case RESET:
                port.getPort().writeBytes("r".getBytes("UTF-8"));
                break;
            default: throw new CommunicationException("Communication Problem...");
        }
    }
    
    public String receiveResponse() throws SerialPortException {
        return port.getPort().readString().trim();
    }
}
