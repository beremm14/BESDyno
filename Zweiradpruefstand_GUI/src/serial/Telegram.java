package serial;

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

     /*************************************
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
    
    private Communication com = Communication.getInstance();
    private Environment env = Environment.getInstance();
    private Port currPort = Port.getInstance();
    
    private List<RawDatapoint> list = new LinkedList<>();

    public static Telegram getInstance() {
        if (instance == null) {
            instance = new Telegram();
        }
        return instance;
    }
    
    private Telegram() {
        this.port = currPort.getPort();
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
            com.sendRequest(Communication.Request.START);
            String response = com.receiveResponse();
            if (response.contains("NO DATA") || response.isEmpty()) {
                throw new Exception("No response");
            } else {
                String s[] = response.split("#");
                env.setEnvTemp(Double.parseDouble(s[0]));
                env.setAirPress(Integer.parseInt(s[1]));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void readBikeTemp() {
        //"EngTemp#FumeTemp"
        
        try {
            com.sendRequest(Communication.Request.ENGINE);
            String response = com.receiveResponse();
            if (response.contains("NO DATA") || response.isEmpty()) {
                throw new Exception("No response");
            } else {
                String s[] = response.split("#");
                env.setEngTemp(Double.parseDouble(s[0]));
                env.setFumeTemp(Double.parseDouble(s[1]));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void readRpmData() {
        //"engCount#wheelCount#time
        try {
            com.sendRequest(Communication.Request.MEASURE);
            String response = com.receiveResponse();
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

    //Getter

    public Environment getEnvironment() {
        return env;
    }
    
    public List<RawDatapoint> getRawDataList() {
        return list;
    }

}
