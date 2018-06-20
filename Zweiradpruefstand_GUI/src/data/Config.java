package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

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

    public Config() {
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
    public void readConfig(BufferedReader r) throws IOException {
        while (r.ready()) {
            String line = r.readLine().trim();
            String s[] = line.split("\t");

            dark = new Scanner(s[0]).nextBoolean();
            hysteresisKmh = new Scanner(s[1]).nextInt();
            hysteresisRpm = new Scanner(s[2]).nextInt();
            hysteresisTime = new Scanner(s[3]).nextInt();
            idleKmh = new Scanner(s[4]).nextInt();
            idleRpm = new Scanner(s[5]).nextInt();
            inertiaCorr = new Scanner(s[6]).nextDouble();
            period = new Scanner(s[7]).nextInt();
            pngHeight = new Scanner(s[8]).nextInt();
            pngWidth = new Scanner(s[9]).nextInt();
            powerCorr = new Scanner(s[10]).nextInt();
            ps = new Scanner(s[11]).nextBoolean();
            startKmh = new Scanner(s[12]).nextInt();
            startRpm = new Scanner(s[13]).nextInt();
            torqueCorr = new Scanner(s[14]).nextInt();
        }
    }

}
