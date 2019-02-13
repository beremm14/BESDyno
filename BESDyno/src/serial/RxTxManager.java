package serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

/**
 *
 * @author emil
 */
public class RxTxManager {

    private boolean init;
    private boolean opened;
    private boolean mac;

    private jssc.SerialPort JSSC;
    private gnu.io.SerialPort RXTX;

    String portName;

    public void portFactory(boolean mac, String port) throws PortInUseException, NoSuchPortException, Exception {
        this.init = true;
        this.mac = mac;
        this.portName = port;

        if (!mac) {
            JSSC = new jssc.SerialPort(port);
        }
    }

    public void openPort() throws Exception {
        if (init) {
            if (mac) {
                CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
                if (portIdentifier.isCurrentlyOwned()) {
                    throw new PortInUseException();
                } else {
                    CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
                    if (commPort instanceof gnu.io.SerialPort) {
                        RXTX = (gnu.io.SerialPort) commPort;
                        opened = true;
                    } else {
                        throw new Exception("Internal Error");
                    }
                }
            } else {
                JSSC.openPort();
                opened = true;
            }
        } else {
            throw new Exception("Port not initialized");

        }
    }

    public void setParams(int baudrate, int databits, int stopbits, int parity) throws Exception {
        if (opened) {
            if (mac) {
                RXTX.setSerialPortParams(baudrate, databits, stopbits, parity);
            } else {
                JSSC.setParams(baudrate, databits, stopbits, parity);
            }
        } else {
            throw new Exception("Port not opened");
        }
    }
    
    public void closePort() throws Exception {
        if (opened) {
            if (mac) {
                RXTX.close();
            } else {
                JSSC.closePort();
            }
        } else {
            throw new Exception("Port already closed");
        }
    }
    
    public Object getPort() {
        if (mac) {
            return RXTX;
        } else {
            return JSSC;
        }
    }

}
