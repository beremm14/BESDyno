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

    private int idleKmh;
    private int hysteresisKmh;
    private int startKmh;

    private int idleRpm;
    private int hysteresisRpm;
    private int startRpm;

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

    public int getIdleKmh() {
        return idleKmh;
    }

    public int getHysteresisKmh() {
        return hysteresisKmh;
    }

    public int getStartKmh() {
        return startKmh;
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
        this.idleKmh = idleKmh;
    }

    public void setHysteresisKmh(int hysteresisKmh) {
        this.hysteresisKmh = hysteresisKmh;
    }

    public void setStartKmh(int startKmh) {
        this.startKmh = startKmh;
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

    public void writeJson(BufferedWriter w) throws IOException {

        final JsonObjectBuilder b = Json.createObjectBuilder();

        b.add("Dark", dark)
                .add("Hysteresis Km/h", hysteresisKmh)
                .add("Hysteresis Rpm", hysteresisRpm)
                .add("Hysteresis Time", hysteresisTime)
                .add("Idle Km/h", idleKmh)
                .add("Idle Rpm", idleRpm)
                .add("Inertia", inertiaCorr)
                .add("Period", period)
                .add("PNG Height", pngHeight)
                .add("PNG Width", pngWidth)
                .add("Power Correction Factor", powerCorr)
                .add("PS", ps)
                .add("Start Km/h", startKmh)
                .add("Start Rpm", startRpm)
                .add("Torque Correction Factor", torqueCorr);

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
        hysteresisKmh = json.getInt("Hysteresis Km/h");
        hysteresisRpm = json.getInt("Hysteresis Rpm");
        hysteresisTime = json.getInt("Hysteresis Time");
        idleKmh = json.getInt("Idle Km/h");
        idleRpm = json.getInt("Idle Rpm");
        inertiaCorr = json.getJsonNumber("Inertia").doubleValue();
        period = json.getInt("Period");
        pngHeight = json.getInt("PNG Height");
        pngWidth = json.getInt("PNG Width");
        powerCorr = json.getInt("Power Correction Factor");
        ps = json.getBoolean("PS");
        startKmh = json.getInt("Start Km/h");
        startRpm = json.getInt("Start Rpm");
        torqueCorr = json.getInt("Torque Correction Factor");

    }

}
