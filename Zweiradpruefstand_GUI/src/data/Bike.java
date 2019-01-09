package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author emil
 */
public class Bike {

    private static Bike instance = null;
    
    private String vehicleName;

    private boolean twoStroke;
    private boolean automatic;

    private boolean measRpm;
    
    private boolean startStopMethod;

    private final Date date = Calendar.getInstance().getTime();
    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
    private String timePoint = null;

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

}
