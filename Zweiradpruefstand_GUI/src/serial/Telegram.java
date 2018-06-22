package serial;

import jssc.SerialPort;
import jssc.SerialPortException;
import measure.Environment;

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
     **************************************/
    private final jssc.SerialPort port;

    public Telegram(SerialPort port) {
        this.port = port;
        try {
            port.setParams(SerialPort.BAUDRATE_57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch (SerialPortException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public Environment getEnvData() throws Exception {
        //EnvTemp, EngTemp, Airpress
        boolean error = false;
        double envTemp = 0;
        double engTemp = 0;
        int airPress = 0;

        try {
            port.writeString("START");
            String response = port.readString().trim();
            if (response.contains("NO DATA") || response.isEmpty()) {
                error = true;
                throw new Exception("No response");
            } else {
                try {
                    String s[] = response.split("#");
                    envTemp = Double.parseDouble(s[0]);
                    engTemp = Double.parseDouble(s[1]);
                    airPress = Integer.parseInt(s[2]);
                } catch (NumberFormatException e) {
                    error = true;
                    e.printStackTrace(System.err);
                }
            }
        } catch (Exception e) {
            error = true;
            e.printStackTrace(System.err);
        }
            if (!error) {
                return new Environment(envTemp, engTemp, airPress);
            } else {
                throw new Exception("Error: Environment Data");
            }
    }

}
