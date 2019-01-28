package data;

/**
 *
 * @author emil
 */
public class Environment {
    
    private static Environment instance = null;
    
    private double envTemp; //°C
    private double engTemp; //°C
    private double fumeTemp; //°C
    private double airPress;   //Pa
    private double altitude;   //m, wenn nicht eingegeben: Näherungswert
    
    private boolean normEnable; //enables DIN70020-Calculation
    
    public static Environment getInstance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    private Environment() {
    }
    
    //Getter
    public double getEnvTemp() {
        return envTemp;
    }

    public double getEngTemp() {
        return engTemp;
    }
    
    public double getFumeTemp() {
        return fumeTemp;
    }

    public double getAirPress() {
        return airPress;
    }

    public double getAltitude() {
        return altitude;
    }
    
    public String envTempToString() {
        return envTemp + "°C";
    }
    
    public String engTempToString() {
        return engTemp + "°C";
    }
    
    public String fumeTempToString() {
        return fumeTemp + "°C";
    }
    
    public String airPressToString() {
        return airPress + "hPa";
    }
    
    public String altitudeToString() {
        return altitude + "m";
    }
    
    public boolean isNormEnable() {
        return normEnable;
    }

    //Setter
    public void setEnvTemp(double envTemp) {
        this.envTemp = envTemp;
    }

    public void setEngTemp(double engTemp) {
        this.engTemp = engTemp;
    }
    
    public void setFumeTemp(double fumeTemp) {
        this.fumeTemp = fumeTemp;
    } 

    public void setAirPress(double airPress) {
        this.airPress = airPress;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    
    public void setNormEnable(boolean normEnable) {
        this.normEnable = normEnable;
    }
    
}
