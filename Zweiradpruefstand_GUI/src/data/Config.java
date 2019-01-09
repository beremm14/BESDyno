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

    private int pngHeight;
    private int pngWidth;

    private int powerCorr;
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

    public String getPowerUnit() {
        return ps ? "PS" : "kW";
    }

    public int getPngHeight() {
        return pngHeight;
    }

    public int getPngWidth() {
        return pngWidth;
    }

    public int getPowerCorr() {
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

    //Setter
    public void setPs(boolean ps) {
        this.ps = ps;
    }

    public void setPngHeight(int pngHeight) {
        this.pngHeight = pngHeight;
    }

    public void setPngWidth(int pngWidth) {
        this.pngWidth = pngWidth;
    }

    public void setPowerCorr(int powerCorr) {
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
                .add("Start Velo", startVelo)
                .add("Start Rpm", startRpm)
                .add("Stop Velo", stopVelo)
                .add("Stop Rpm", stopRpm)
                .add("Torque Correction Factor", torqueCorr)
                .add("Velocity", writeVelocity());

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
        powerCorr = json.getInt("Power Correction Factor");
        ps = json.getBoolean("PS");
        startVelo = json.getInt("Start Velo");
        startRpm = json.getInt("Start Rpm");
        stopVelo = json.getInt("Stop Velo");
        stopRpm = json.getInt("Stop Rpm");
        torqueCorr = json.getInt("Torque Correction Factor");
        velocity = readVelocity(json.getInt("Velocity"));
    }

}
