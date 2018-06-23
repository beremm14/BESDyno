package measure;

/**
 *
 * @author emil
 */
public class Environment {
    
    private final double envTemp; //°C
    private final double engTemp; //°C
    private final int airPress;   //hPa
    private final int altitude;   //m, wenn nicht eingegeben: Näherungswert

    public Environment(double envTemp, double engTemp, int airPress, int altitude) {
        this.envTemp = envTemp;
        this.engTemp = engTemp;
        this.airPress = airPress;
        this.altitude = altitude;
    }

    public Environment(double envTemp, double engTemp, int airPress) {
        this.envTemp = envTemp;
        this.engTemp = engTemp;
        this.airPress = airPress;
        this.altitude = calcAltitude(airPress);
    }

    public double getEnvTemp() {
        return envTemp;
    }

    public double getEngTemp() {
        return engTemp;
    }

    public int getAirPress() {
        return airPress;
    }

    public int getAltitude() {
        return altitude;
    }
    
    private int calcAltitude(int airpress) {
        double p = (double)airpress;
        
        //Internationale Höhenformel nach h umgeformt
        double result =  (1-Math.pow((p/1013.25), (1/5.255))) * (288150/6.5);
        return Math.round((float)result);
    }
    
}
