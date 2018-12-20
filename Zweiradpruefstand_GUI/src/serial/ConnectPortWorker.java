package serial;

import java.util.logging.Level;
import javax.swing.SwingWorker;
import logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 *
 * @author emil
 */
public class ConnectPortWorker extends SwingWorker<Object, jssc.SerialPort> {

    private static final Logger LOG = Logger.getLogger(ConnectPortWorker.class.getName());
    private static ConnectPortWorker instance = null;

    private final String port;
    

    public ConnectPortWorker(String port) {
        this.port = port;
    }

    @Override
    protected jssc.SerialPort doInBackground() throws Exception {
        jssc.SerialPort serialPort = new jssc.SerialPort(port);
        if (serialPort.openPort() == true) {
            LOG.info("Connected");
            serialPort.setParams(SerialPort.BAUDRATE_57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            LOG.info("Params set: Baudrate: " + SerialPort.BAUDRATE_57600 + " Databits: " + SerialPort.DATABITS_8 + " Stopbits: " + SerialPort.STOPBITS_1 + " Parity: NONE");
            return serialPort;
        } else {
            throw new jssc.SerialPortException(port, "openPort", "return value false");
        }
    }

}
