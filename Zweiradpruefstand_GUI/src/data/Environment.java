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
    private double airPress;   //hPa
    private double altitude;   //m, wenn nicht eingegeben: Näherungswert
    
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
    
}
