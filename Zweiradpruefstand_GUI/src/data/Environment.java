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
    private int airPress;   //hPa
    private int altitude;   //m, wenn nicht eingegeben: Näherungswert
    
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

    public int getAirPress() {
        return airPress;
    }

    public int getAltitude() {
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

    public void setAirPress(int airPress) {
        this.airPress = airPress;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }
    
    public void setAltitude() {
        this.altitude = calcAltitude(airPress);
    }
    
    //Calculates h(p) without temperature
    private int calcAltitude(int airpress) {
        double p = (double)airpress;
        
        //Internationale Höhenformel nach h umgeformt
        double result =  (1 - Math.pow((p/1013.25), (1/5.255)) ) * (288150/6.5);
        return Math.round((float)result);
    }
    
}
