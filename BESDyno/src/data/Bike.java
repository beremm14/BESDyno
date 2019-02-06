package data;

/**
 *
 * @author emil
 */
public class Bike {

    private static Bike instance = null;
    
    private String vehicleName = "BESDyno";

    private boolean twoStroke;
    private boolean automatic;

    private boolean measRpm;
    
    private boolean startStopMethod;
    
    private boolean measTemp;

    public static Bike getInstance() {
        if (instance == null) {
            instance = new Bike();
        }
        return instance;
    }

    private Bike() {
    }

    //Getter
    public String getVehicleName() {
        return vehicleName;
    }

    public boolean isTwoStroke() {
        return twoStroke;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public boolean isMeasRpm() {
        return measRpm;
    }
    
    public boolean isMeasTemp() {
        return measTemp;
    }
    
    public boolean isStartStopMethod() {
        return startStopMethod;
    }

    //Setter
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public void setTwoStroke(boolean twoStroke) {
        this.twoStroke = twoStroke;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public void setMeasRpm(boolean measRpm) {
        this.measRpm = measRpm;
    }
    
    public void setStartStopMethode(boolean startStopMethod) {
        this.startStopMethod = startStopMethod;
    }
    
    public void setMeasTemp(boolean measTemp) {
        this.measTemp = measTemp;
    }

}
