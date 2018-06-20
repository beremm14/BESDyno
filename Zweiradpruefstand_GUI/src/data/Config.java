package data;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author emil
 */
public class Config {
    
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

    
    public Config() {}

    
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

    public double getInertiaCorr() {
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
    public void writeLine(BufferedWriter w) throws IOException {
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
    
    
}
