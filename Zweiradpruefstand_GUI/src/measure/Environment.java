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

    
    private int calcAltitude(int p) {
        int rv = 0;
         if (p>735) {
             rv = 2500;
         } else if(p>759) {
             rv = 2240;
         } else if(p>783) {
             rv = 2000;
         } else if(p>835) {
             rv = 1500;
         } else if(p>891) {
             rv = 1000;
         } else if(p>902) {
             rv = 900;
         } else if(p>914) {
             rv = 800;
         } else if(p>926) {
             rv = 700;
         } else if(p>938) {
             rv = 600;
         } else if(p>950) {
             rv = 500;
         } else if(p>962) {
             rv = 400;
         } else if(p>975) {
             rv = 300;
         } else if(p>987) {
             rv = 200;
         } else if(p>1000) {
             rv = 100;
         } else if(p>1013) {
             rv = 0;
         }
         return rv;
    }
    
    
}
