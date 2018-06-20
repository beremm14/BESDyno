package data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author emil
 */
public class Bike {
    
    private String vehicleName;
    private boolean twoStroke;
    private boolean automatic;
    
    private List<Datapoint> engineData = new LinkedList<>();
    private List<Datapoint> schleppData = new LinkedList<>();
    
    private final Date date = Calendar.getInstance().getTime();
    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
    private String time = null;

    
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
        return engineData;
    }

    public List<Datapoint> getSchleppData() {
        return schleppData;
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

    public void setEngineData(List<Datapoint> engineData) {
        this.engineData = engineData;
    }

    public void setSchleppData(List<Datapoint> schleppData) {
        this.schleppData = schleppData;
    }
    
    
    //ArrayList-Methods
    public int size(List<Datapoint> list) {
        return list.size();
    }

    public Datapoint get(List<Datapoint> list, int index) {
        return list.get(index);
    }

    public Datapoint set(List<Datapoint> list, int index, Datapoint element) {
        return list.set(index, element);
    }

    public boolean add(List<Datapoint> list, Datapoint e) {
        return list.add(e);
    }

    public String toString(List<Datapoint> list) {
        return list.toString();
    }
    
    
    //Writout
    private void writeList(BufferedWriter w, List<Datapoint> list) throws IOException {
        for (Datapoint d: list) {
            d.writeLine(w);
            w.newLine();
        }
    }
    
    private void writeHeader(BufferedWriter w) throws IOException {
        time = df.format(date);
        w.write(time);
        w.newLine();
        w.write(vehicleName);
        w.newLine();
        w.write(twoStroke + "");
        w.newLine();
        w.write(automatic + "");
        w.newLine();
    }
    
    public void writeFile(BufferedWriter w, boolean engine, boolean schlepp) throws IOException, IllegalArgumentException {
        writeHeader(w);
        
        if (!engine && !schlepp) {
            throw new IllegalArgumentException("Keine Messdaten aktiv!");
        }
        
        if (engine) {
            w.write("---engine---");
            w.newLine();
            writeList(w, engineData);
            w.newLine();
        } else {
            w.write("------------");
            w.newLine();
        }
        
        if (schlepp) {
            w.write("---schlepp---");
            w.newLine();
            writeList(w, schleppData);
            w.newLine();
        } else {
            w.write("-------------");
            w.newLine();
        }
    }
    
}
