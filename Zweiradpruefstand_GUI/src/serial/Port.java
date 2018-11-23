package serial;

import logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 *
 * @author emil
 */
public class Port {

    private static final Logger LOG = Logger.getLogger(Port.class.getName());
    private static Port instance = null;

    private jssc.SerialPort port;
    
    public static Port getInstance() {
        if (instance == null) {
            instance = new Port();
        }
        return instance;
    }

    private Port() {
    }
    
    public SerialPort getPort() {
        return port;
    }

    public void setPort(SerialPort port) {
        this.port = port;
    }

    public void connectPort(String serialPort) throws SerialPortException {
        port = new jssc.SerialPort(serialPort);
        if (port.openPort() == false) {
            throw new jssc.SerialPortException(serialPort, "openPort", "return value false");
        }
        Telegram.getInstance().initializeCommunication();
    }

    public void disconnectPort() throws SerialPortException, Exception {
        if (port == null || !port.isOpened()) {
            throw new Exception("Interner Fehler!");
        }
        if (port.closePort() == false) {
            throw new jssc.SerialPortException(null, "closePort", "return value false");
        }
    }

}
