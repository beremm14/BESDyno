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
    private double altitude;   //m
    
    private boolean normEnable = false; //enables DIN70020-Calculation
    
    public static Environment getInstance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    private Environment() {
    }
    
    //Getter
    public double getEnvTempC() {
        return envTemp;
    }

    public double getEngTempC() {
        return engTemp;
    }
    
    public double getFumeTempC() {
        return fumeTemp;
    }
    
    public double getEnvTempF() {
        return (envTemp * (9.0 / 5.0)) + 32;
    }
    
    public double getEngTempF() {
        return (engTemp * (9.0 / 5.0)) + 32;
    }
    
    public double getFumeTempF() {
        return (fumeTemp * (9.0 / 5.0)) + 32;
    }

    public double getAirPress() {
        return airPress;
    }

    public double getAltitude() {
        return altitude;
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
