package serial;

import data.Datapoint;
import data.RawDatapoint;
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
    
    private RawDatapoint rawData;
    private Datapoint data;
    
    private double engPower;
    private double wheelPower;
    private int engRpm;
    private int wheelRpm;
    
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

    //Communication
    public void readEnvData() throws Exception {
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
    
    public void readEngTemp() {
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

    //Getter
    public double getEngTemp() {
        return engTemp;
    }

    public double getEnvTemp() {
        return envTemp;
    }

    public int getAirPress() {
        return airPress;
    }

    public RawDatapoint getRawData() {
        return rawData;
    }
    
    public Datapoint getData() {
        return data;
    }

    public double getEngPower() {
        return engPower;
    }

    public double getWheelPower() {
        return wheelPower;
    }

    public int getEngRpm() {
        return engRpm;
    }

    public int getWheelRpm() {
        return wheelRpm;
    }

}
