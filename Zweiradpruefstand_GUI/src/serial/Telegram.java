package serial;

import data.Environment;
import data.RawDatapoint;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 *
 * @author emil
 */
public class Telegram extends JFrame implements SerialPortEventListener {

    /**************************************
     * Aufbau der Datenübertragung:       *
     * Request: START                     *
     * Response: Temperaturen             *
     * Answer: OK ? continue : try again  *
     * Request: MEASURE                   *
     * Response: Drehzahlen und Zeit      *
     * Request: ENGINE                    *
     * Response: Motor-/Abgastemperatur   *
     **************************************/
    
    private static Telegram instance = null;
    
    private static final Logger LOG = Logger.getLogger(Telegram.class.getName());
    
    private final jssc.SerialPort port;
    
    private String response = "";    
    private List<RawDatapoint> list = new LinkedList<>();

    public static Telegram getInstance() {
        if (instance == null) {
            instance = new Telegram();
        }
        return instance;
    }
    
    private Telegram() {
        this.port = Port.getInstance().getPort();
        try {
            port.setParams(SerialPort.BAUDRATE_57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            port.addEventListener((SerialPortEventListener) this);
        } catch (SerialPortException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public void initializeCommunication(){
        int count = 0;
        try {
            while (response.isEmpty()) {
                count++;
                Arduino.getInstance().sendRequest(Arduino.Request.INIT);
                LOG.warning("Initialize: Try " + count);
            }
        } catch (Throwable th) {
            LOG.severe(th);
        }
    }

    //Communication
    public void readEnvData() {
        //"EnvTemp#Airpress#Altitude"

        try {
            Arduino.getInstance().sendRequest(Arduino.Request.START);
            LOG.info("Telegram: 'START' sent");
            if (!response.isEmpty()) {
                LOG.fine("Telegram received: " + response);
            }
            if (response.contains("NO DATA") || response.isEmpty()) {
                LOG.severe("No response");
            } else {
                String s[] = response.split("#");
                Environment.getInstance().setEnvTemp(Double.parseDouble(s[0]));
                Environment.getInstance().setAirPress(Double.parseDouble(s[1]));
                Environment.getInstance().setAltitude(Double.parseDouble(s[2]));
            }
        } catch (Exception e) {
            LOG.warning(e);
        }
    }
    
    public void readBikeTemp() {
        //"EngTemp#FumeTemp"
        
        try {
            Arduino.getInstance().sendRequest(Arduino.Request.ENGINE);
            //String response = Arduino.getInstance().receiveResponse();
            if (response.contains("NO DATA") || response.isEmpty()) {
                throw new Exception("No response");
                
            } else {
                String s[] = response.split("#");
                Environment.getInstance().setEngTemp(Double.parseDouble(s[0]));
                Environment.getInstance().setFumeTemp(Double.parseDouble(s[1]));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void readRpmData() {
        //"engCount#wheelCount#time
        try {
            Arduino.getInstance().sendRequest(Arduino.Request.MEASURE);
            //String response = Arduino.getInstance().receiveResponse();
            if (response.contains("NO DATA") || response.isEmpty()) {
                throw new Exception("No response");
            } else {
                String s[] = response.split("#");
                list.add(new RawDatapoint(s[0], s[1], s[2]));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void serialEvent(SerialPortEvent spe) {
        LOG.info("Serial Event happened");
                try {
                    response = Port.getInstance().getPort().readString().trim();
                } catch (SerialPortException ex) {
                    LOG.severe(ex);
                }
    }

}
