package development;

import data.Bike;
import data.Database;
import data.RawDatapoint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import logging.Logger;
import measure.Calculate;

/**
 *
 * @author emil
 */
public class TestCSV {

    private static final Logger LOG = Logger.getLogger(TestCSV.class.getName());

    private final Database data = Database.getInstance();
    private final Calculate calc = new Calculate();

    private Date date;
    private DateFormat df = new SimpleDateFormat("yy.MM.dd-HH.mm.ss.SSS");
    
    public TestCSV() {
        this.date = Calendar.getInstance().getTime();
        calc.calcPower();
    }
    
    public void writeFiles() {
        //Engine
        try (BufferedWriter w = new BufferedWriter(new FileWriter(createFile("Engine")))) {
            writeEngine(w);
        } catch (Exception ex) {
            LOG.warning(ex);
        }
        
        //Wheel
        try (BufferedWriter w = new BufferedWriter(new FileWriter(createFile("Wheel")))) {
            writeWheel(w);
        } catch (Exception ex) {
            LOG.warning(ex);
        }
        
        //Raw
        try (BufferedWriter w = new BufferedWriter(new FileWriter(createFile("Raw")))) {
            writeRaw(w);
        } catch (Exception ex) {
            LOG.warning(ex);
        }
    }

    private File createFile(String name) throws Exception {
        File file = null;
        File home;
        File folder;

        home = new File(System.getProperty("user.home"));

        if (home != null && home.exists()) {
            folder = new File(home + File.separator + "Bike-Files" + File.separator + "Test_Files");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            file = new File(folder + File.separator + Bike.getInstance().getVehicleName() + "_" + name + "_" + df.format(date) + ".csv");
        }

        return file;
    }

    private void writeEngine(BufferedWriter w) throws IOException {
        //engPower;engTorque;engRpm

        for (int i = 0; i < data.getEngRpmList().size(); i++) {
            w.write(data.getEngPowerList().get(i) + "");
            w.write(",");
            w.write(data.getEngTorList().get(i) + "");
            w.write(",");
            w.write(data.getEngRpmList().get(i) + "");
            w.newLine();
        }
    }

    private void writeWheel(BufferedWriter w) throws IOException {
        //velocity;wheelPower;wheelTorque;wheelRpm

        for (int i = 0; i < data.getWheelRpmList().size(); i++) {
            w.write(data.getVelList().get(i) + "");
            w.write(",");
            w.write(data.getWheelPowerList().get(i) + "");
            w.write(",");
            w.write(data.getWheelTorList().get(i) + "");
            w.write(",");
            w.write(data.getWheelRpmList().get(i) + "");
            w.newLine();
        }
    }

    private void writeRaw(BufferedWriter w) throws IOException {
        //engCounts;wheelCounts;time

        for (RawDatapoint rdp : data.getRawList()) {
            w.write(rdp.getEngCount() + "");
            w.write(",");
            w.write(rdp.getWheelCount() + "");
            w.write(",");
            w.write(rdp.getTime() + "");
            w.newLine();
        }
    }

}
