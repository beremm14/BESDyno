package serial;

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
     * Response: * Drehzahlen und Zeit    *
     * Request: ENGINE                    *
     * Response: Motortemperatur          *
     **************************************/
    
    private final jssc.SerialPort port;
    
    private double engTemp;
    private double envTemp;
    private int airPress;

    public Telegram(SerialPort port) {
        this.port = port;
        try {
            port.setParams(SerialPort.BAUDRATE_57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch (SerialPortException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void getEnvData() throws Exception {
        //EnvTemp, Airpress

        try {
            port.writeString("START");
            String response = port.readString().trim();
            if (response.contains("NO DATA") || response.isEmpty()) {
                throw new Exception("No response");
            } else {
                String s[] = response.split("#");
                envTemp = Double.parseDouble(s[0]);
                airPress = Integer.parseInt(s[1]);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void getEngTemp() {
        try {
            port.writeString("ENGINE");
            String response = port.readString().trim();
            if (response.contains("NO DATA") || response.isEmpty()) {
                throw new Exception("No response");
            } else {
                engTemp = Double.parseDouble(response);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

}
