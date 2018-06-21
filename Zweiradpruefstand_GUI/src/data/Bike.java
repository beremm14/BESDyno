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

    private String vehicleName;
    
    private boolean twoStroke;
    private boolean automatic;
    
    private boolean measRpm;
    private boolean schleppEnable;

    private List<Datapoint> list = new LinkedList<>();

    private final Date date = Calendar.getInstance().getTime();
    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
    private String timePoint = null;

    public Bike(String vehicleName, boolean twoStroke, boolean automatic, boolean measRpm, boolean schleppEnable) {
        this.vehicleName = vehicleName;
        this.twoStroke = twoStroke;
        this.automatic = automatic;
        this.measRpm = measRpm;
        this.schleppEnable = schleppEnable;
    }
    
    public Bike(String vehicleName, boolean twoStroke, boolean automatic) {
        this.vehicleName = vehicleName;
        this.twoStroke = twoStroke;
        this.automatic = automatic;
    }

    public Bike() {}

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

    public List<Datapoint> getEngineData() {
        return list;
    }
    
    public boolean isMeasRpm() {
        return measRpm;
    }
    
    public boolean isSchleppEnable() {
        return schleppEnable;
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

    public void setSchleppEnable(boolean schleppEnable) {
        this.schleppEnable = schleppEnable;
    }

    //ArrayList-Methods
    public int size() {
        return list.size();
    }

    public Datapoint get(int index) {
        return list.get(index);
    }

    public Datapoint set(int index, Datapoint element) {
        return list.set(index, element);
    }

    public boolean add(Datapoint e) {
        return list.add(e);
    }

    @Override
    public String toString() {
        return list.toString();
    }

    //Writout
    private void writeList(BufferedWriter w) throws IOException {
        //Time, RPM, WSS
        for (Datapoint d : list) {
            d.writeLine(w);
            w.newLine();
        }
    }

    private void writeHeader(BufferedWriter w) throws IOException {
        w.write("BES-Data");
        timePoint = df.format(date);
        w.write(timePoint);
        w.newLine();
        w.write(vehicleName);
        w.newLine();
        w.write(String.format("%b", twoStroke));
        w.newLine();
        w.write(String.format("%b", automatic));
        w.newLine();
        w.write(String.format("%b", measRpm));
        w.newLine();
        w.write(String.format("%b", schleppEnable));
        w.newLine();
    }

    public void writeFile(BufferedWriter w) throws IOException, IllegalArgumentException {
        writeHeader(w);
        w.newLine();
        writeList(w);
        w.newLine();
    }

    //Read
    public void readFile(BufferedReader r) throws IOException, Exception {
        list.clear();

        String line = r.readLine().trim();
        if (!line.contains("BES-Data")) {
            throw new Exception("Not supported file");
        }

        timePoint = r.readLine().trim();
        vehicleName = r.readLine().trim();
        twoStroke = new Scanner(r.readLine().trim()).nextBoolean();
        automatic = new Scanner(r.readLine().trim()).nextBoolean();
        measRpm = new Scanner(r.readLine().trim()).nextBoolean();
        schleppEnable = new Scanner(r.readLine().trim()).nextBoolean();

        while (r.ready()) {
            line = r.readLine().trim();
            if (line.contains("#") || line.isEmpty()) {
                continue;
            }

            String s[] = line.split("\t");
            double time = new Scanner(s[0]).nextDouble();
            double rpm = new Scanner(s[1]).nextDouble();
            double wss = new Scanner(s[2]).nextDouble();
            add(new Datapoint(wss, rpm, time));
        }
    }

}
