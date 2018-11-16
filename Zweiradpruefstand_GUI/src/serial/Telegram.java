
import data.Environment;
import data.RawDatapoint;
import java.util.LinkedList;
import java.util.List;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 *
 * @author emil
 */
public class Telegram {

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
    
    private final jssc.SerialPort port;
    
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
        } catch (SerialPortException ex) {
            ex.printStackTrace(System.err);
        }
    }

    //Communication
    public void readEnvData() {
        //"EnvTemp#Airpress"

        try {
            Arduino.getInstance().sendRequest(Arduino.Request.START);
            String response = Arduino.getInstance().receiveResponse();
            if (response.contains("NO DATA") || response.isEmpty()) {
                throw new Exception("No response");
            } else {
                String s[] = response.split("#");
                Environment.getInstance().setEnvTemp(Double.parseDouble(s[0]));
                Environment.getInstance().setAirPress(Integer.parseInt(s[1]));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void readBikeTemp() {
        //"EngTemp#FumeTemp"
        
        try {
            Arduino.getInstance().sendRequest(Arduino.Request.ENGINE);
            String response = Arduino.getInstance().receiveResponse();
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
            String response = Arduino.getInstance().receiveResponse();
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

}
