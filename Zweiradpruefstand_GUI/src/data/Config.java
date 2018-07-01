package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author emil
 */
public class Config {
    
    private static Config instance = null;

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
            instance = new Config();
        }
        return instance;
    }

    private Config() {
    }

    //Getter
    public boolean isPs() {
        return ps;
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

    //Writeout
    public void writeConfig(BufferedWriter w) throws IOException {
        w.write(String.format("%b", dark));
        w.write("\t");
        w.write(String.format("%d", hysteresisKmh));
        w.write("\t");
        w.write(String.format("%d", hysteresisRpm));
        w.write("\t");
        w.write(String.format("%d", hysteresisTime));
        w.write("\t");
        w.write(String.format("%d", idleKmh));
        w.write("\t");
        w.write(String.format("%d", idleRpm));
        w.write("\t");
        w.write(String.format("%f", inertiaCorr));
        w.write("\t");
        w.write(String.format("%d", period));
        w.write("\t");
        w.write(String.format("%d", pngHeight));
        w.write("\t");
        w.write(String.format("%d", pngWidth));
        w.write("\t");
        w.write(String.format("%d", powerCorr));
        w.write("\t");
        w.write(String.format("%b", ps));
        w.write("\t");
        w.write(String.format("%d", startKmh));
        w.write("\t");
        w.write(String.format("%d", startRpm));
        w.write("\t");
        w.write(String.format("%d", torqueCorr));
    }

    //Read
    public void readConfig(BufferedReader r) throws IOException, NumberFormatException, Exception {
        while (r.ready()) {
            String line = r.readLine().trim();
            if (line.isEmpty())
                throw new Exception("Config-File Error!");
            
            String s[] = line.split("\t");

            dark = Boolean.parseBoolean(s[0]);
            hysteresisKmh = Integer.parseInt(s[1]);
            hysteresisRpm = Integer.parseInt(s[2]);
            hysteresisTime = Integer.parseInt(s[3]);
            idleKmh = Integer.parseInt(s[4]);
            idleRpm = Integer.parseInt(s[5]);
            inertiaCorr = Double.parseDouble(s[6]);
            period = Integer.parseInt(s[7]);
            pngHeight = Integer.parseInt(s[8]);
            pngWidth = Integer.parseInt(s[9]);
            powerCorr = Integer.parseInt(s[10]);
            ps = Boolean.parseBoolean(s[11]);
            startKmh = Integer.parseInt(s[12]);
            startRpm = Integer.parseInt(s[13]);
            torqueCorr = Integer.parseInt(s[14]);
        }
    }

}
