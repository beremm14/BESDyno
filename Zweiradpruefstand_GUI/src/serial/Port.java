package serial;

import java.util.logging.Level;
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
        if (port.openPort() == true) {
            LOG.info("Connected");
            port.setParams(SerialPort.BAUDRATE_57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            LOG.info("Params set: Baudrate: " + SerialPort.BAUDRATE_57600 + " Databits: " + SerialPort.DATABITS_8 + " Stopbits: " + SerialPort.STOPBITS_1 + " Parity: NONE");
            Telegram.getInstance().initializeCommunication();
        } else {
            throw new jssc.SerialPortException(serialPort, "openPort", "return value false");
        }
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
