package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Map;
import logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

/**
 *
 * @author emil
 */
public class Config {

    private static Config instance = null;
    private static final Logger LOG = Logger.getLogger(Config.class.getName());

    private boolean ps;
    private boolean celcius;

    private int pngHeight;
    private int pngWidth;

    private double powerCorr;
    private int torqueCorr;
    private double inertiaCorr;

    private boolean dark;

    private int period;
    private int hysteresisTime;

    private int idleVelo;
    private int hysteresisVelo;
    private int startVelo;
    private int stopVelo;

    private int idleRpm;
    private int hysteresisRpm;
    private int startRpm;
    private int stopRpm;
    
    private int warningEngTemp;
    private int warningExhTemp;
    
    private double arduinoVersion = 0;
    
    private Velocity velocity;
    
    public enum Velocity {
        MPS, KMH, MIH
    };

    public static Config getInstance() {
        if (instance == null) {
            throw new RuntimeException("Instance not initialized");
        }
        return instance;
    }
    
    public static Config createInstance(InputStream fis) throws Exception {
        instance = new Config();
        instance.readJson(fis);
        return instance;
    }

    //Getter
    public boolean isPs() {
        return ps;
    }
    
    public boolean isCelcius() {
        return celcius;
    }

    public String getPowerUnit() {
        return ps ? "PS" : "kW";
    }
    
    public String getTempUnit() {
        return celcius ? "°C" : "°F";
    }
    
    public String getVeloUnit() {
        switch(velocity) {
            case MPS: return "m/s";
            case MIH: return "mi/h";
            case KMH: return "km/h";
            default: throw new RuntimeException("No Velocity Unit defined...");
        }
    }

    public int getPngHeight() {
        return pngHeight;
    }

    public int getPngWidth() {
        return pngWidth;
    }

    public double getPowerCorr() {
        return powerCorr;
    }

    public int getTorqueCorr() {
        return torqueCorr;
    }

    public double getInertia() {
        return inertiaCorr;
    }

    public boolean isDark() {
        return dark;
    }

    public int getPeriod() {
        return period;
    }

    public int getHysteresisTime() {
        return hysteresisTime;
    }

    public int getIdleVelo() {
        return idleVelo;
    }

    public int getHysteresisVelo() {
        return hysteresisVelo;
    }

    public int getStartVelo() {
        return startVelo;
    }

    public int getIdleRpm() {
        return idleRpm;
    }

    public int getHysteresisRpm() {
        return hysteresisRpm;
    }

    public int getStartRpm() {
        return startRpm;
    }
    
    public double getArduinoVersion() {
        return arduinoVersion;
    }
    
    public Velocity getVelocity() {
        return velocity;
    }

    public int getStopVelo() {
        return stopVelo;
    }

    public int getStopRpm() {
        return stopRpm;
    }

    public int getWarningExhTemp() {
        return warningExhTemp;
    }
    
    public int getWarningEngTemp() {
        return warningEngTemp;
    }

    //Setter
    public void setPs(boolean ps) {
        this.ps = ps;
    }
    
    public void setCelcius(boolean celcius) {
        this.celcius = celcius;
    }

    public void setPngHeight(int pngHeight) {
        this.pngHeight = pngHeight;
    }

    public void setPngWidth(int pngWidth) {
        this.pngWidth = pngWidth;
    }

    public void setPowerCorr(double powerCorr) {
        this.powerCorr = powerCorr;
    }

    public void setTorqueCorr(int torqueCorr) {
        this.torqueCorr = torqueCorr;
    }

    public void setInertiaCorr(double inertiaCorr) {
        this.inertiaCorr = inertiaCorr;
    }

    public void setDark(boolean dark) {
        this.dark = dark;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setHysteresisTime(int hysteresisTime) {
        this.hysteresisTime = hysteresisTime;
    }

    public void setIdleKmh(int idleKmh) {
        this.idleVelo = idleKmh;
    }

    public void setHysteresisKmh(int hysteresisKmh) {
        this.hysteresisVelo = hysteresisKmh;
    }

    public void setStartKmh(int startKmh) {
        this.startVelo = startKmh;
    }

    public void setIdleRpm(int idleRpm) {
        this.idleRpm = idleRpm;
    }

    public void setHysteresisRpm(int hysteresisRpm) {
        this.hysteresisRpm = hysteresisRpm;
    }

    public void setStartRpm(int startRpm) {
        this.startRpm = startRpm;
    }
    
    public void setArduinoVersion(double arduinoVersion) {
        this.arduinoVersion = arduinoVersion;
    }
    
    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    public void setIdleVelo(int idleVelo) {
        this.idleVelo = idleVelo;
    }

    public void setHysteresisVelo(int hysteresisVelo) {
        this.hysteresisVelo = hysteresisVelo;
    }

    public void setStartVelo(int startVelo) {
        this.startVelo = startVelo;
    }

    public void setStopVelo(int stopVelo) {
        this.stopVelo = stopVelo;
    }

    public void setStopRpm(int stopRpm) {
        this.stopRpm = stopRpm;
    }

    public void setWarningEngTemp(int warningEngTemp) {
        this.warningEngTemp = warningEngTemp;
    }

    public void setWarningExhTemp(int warningExhTemp) {
        this.warningExhTemp = warningExhTemp;
    }

    
    public int writeVelocity() {
        switch(velocity) {
            case MPS: return 0;
            case KMH: return 1;
            case MIH: return 2;
            default: throw new RuntimeException("Error at writing out unit of velocity...");
        }
    }
    
    public Velocity readVelocity(int velocity) {
        switch(velocity) {
            case 0: return Velocity.MPS;
            case 1: return Velocity.KMH;
            case 2: return Velocity.MIH;
            default: throw new RuntimeException("Error at reading in unit of velocity...");
        }
    }
    
    public void createConfig(BufferedWriter w) throws IOException {
        setVelocity(Velocity.KMH);
        setDark(false);
        setPs(true);
        setCelcius(true);
        setPowerCorr(1);
        setTorqueCorr(1);
        setInertiaCorr(3.7017);
        setPngWidth(1920);
        setPngHeight(1080);
        setPeriod(20);
        setHysteresisTime(2500);
        setIdleVelo(4);
        setHysteresisVelo(4);
        setStartVelo(4);
        setStopVelo(80);
        setIdleRpm(1800);
        setHysteresisRpm(400);
        setStartRpm(2500);
        setStopRpm(9000);
        setWarningEngTemp(95);
        setWarningExhTemp(500);
        writeJson(w);
    }

    public void writeJson(BufferedWriter w) throws IOException {

        final JsonObjectBuilder b = Json.createObjectBuilder();

        b.add("Dark", dark)
                .add("Hysteresis Velo", hysteresisVelo)
                .add("Hysteresis Rpm", hysteresisRpm)
                .add("Hysteresis Time", hysteresisTime)
                .add("Idle Velo", idleVelo)
                .add("Idle Rpm", idleRpm)
                .add("Inertia", inertiaCorr)
                .add("Period", period)
                .add("PNG Height", pngHeight)
                .add("PNG Width", pngWidth)
                .add("Power Correction Factor", powerCorr)
                .add("PS", ps)
                .add("Celcius", celcius)
                .add("Start Velo", startVelo)
                .add("Start Rpm", startRpm)
                .add("Stop Velo", stopVelo)
                .add("Stop Rpm", stopRpm)
                .add("Torque Correction Factor", torqueCorr)
                .add("Velocity", writeVelocity())
                .add("Engine Max Temp", warningEngTemp)
                .add("Exhaust Max Temp", warningExhTemp);

        JsonObject obj = b.build();
        w.write(obj.toString());
        LOG.info("Config-File written: " + obj.toString());
    }

    public void readJson(InputStream fis) throws IOException, Exception {
        JsonObject json;
        
        try (JsonReader jsonReader = Json.createReader(fis)) {
            json = jsonReader.readObject();
        }

        dark = json.getBoolean("Dark");
        hysteresisVelo = json.getInt("Hysteresis Velo");
        hysteresisRpm = json.getInt("Hysteresis Rpm");
        hysteresisTime = json.getInt("Hysteresis Time");
        idleVelo = json.getInt("Idle Velo");
        idleRpm = json.getInt("Idle Rpm");
        inertiaCorr = json.getJsonNumber("Inertia").doubleValue();
        period = json.getInt("Period");
        pngHeight = json.getInt("PNG Height");
        pngWidth = json.getInt("PNG Width");
        powerCorr = json.getJsonNumber("Power Correction Factor").doubleValue();
        ps = json.getBoolean("PS");
        celcius = json.getBoolean("Celcius");
        startVelo = json.getInt("Start Velo");
        startRpm = json.getInt("Start Rpm");
        stopVelo = json.getInt("Stop Velo");
        stopRpm = json.getInt("Stop Rpm");
        torqueCorr = json.getInt("Torque Correction Factor");
        velocity = readVelocity(json.getInt("Velocity"));
        warningEngTemp = json.getInt("Engine Max Temp");
        warningExhTemp = json.getInt("Exhaust Max Temp");
    }

}
