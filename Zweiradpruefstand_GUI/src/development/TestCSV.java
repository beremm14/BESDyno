package development;

import data.Bike;
import data.Config;
import data.Database;
import data.Datapoint;
import data.PreDatapoint;
import data.RawDatapoint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private final Config config = Config.getInstance();

    private Date date;
    private DateFormat df = new SimpleDateFormat("yy.MM.dd-HH.mm.ss.SSS");
    
    public TestCSV() {
        this.date = Calendar.getInstance().getTime();
        calc.calcPower();
    }
    
    public void writeFiles() {
        //Datapoint
        try (BufferedWriter w = new BufferedWriter(new FileWriter(createFile("Datapoint")))) {
            writeDP(w);
        } catch (Exception ex) {
            LOG.warning(ex);
        }
        
        //PreDatapoint
        try (BufferedWriter w = new BufferedWriter(new FileWriter(createFile("PreDatapoint")))) {
            writePDP(w);
        } catch (Exception ex) {
            LOG.warning(ex);
        }
        
        //RawDatapoint
        try (BufferedWriter w = new BufferedWriter(new FileWriter(createFile("RawDatapoint")))) {
            writeRDP(w);
        } catch (Exception ex) {
            LOG.warning(ex);
        }
        
        //Velocity
        try (BufferedWriter w = new BufferedWriter(new FileWriter(createFile("Velocity")))) {
            writeVel(w);
        } catch (Exception ex) {
            LOG.warning(ex);
        }
        
        LOG.info("Test files written");
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

    private void writeDP(BufferedWriter w) throws IOException {
        //power,torque
        for (Datapoint dp : data.getDataList()) {
            w.write(String.format("%.2fW", dp.getPower()));
            w.write(",");
            w.write(String.format("%.2fNm", dp.getTorque()));
            w.newLine();
        }
    }

    private void writePDP(BufferedWriter w) throws IOException {
        //engRpm,wheelRpm,time
        for (PreDatapoint pdp : data.getPreList()) {
            w.write(String.format("%.2fU/min", pdp.getEngRpm()));
            w.write(",");
            w.write(String.format("%.2fU/min", pdp.getWheelRpm()));
            w.write(",");
            w.write(String.format("%.2fms", pdp.getTime() * 1000));
        }
    }

    private void writeRDP(BufferedWriter w) throws IOException {
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
    
    private void writeVel(BufferedWriter w) throws IOException {
        //velocity
        for (Double vel : data.getVelList()) {
            w.write(String.format("%.2f" + config.getVeloUnit(), vel));
        }
    }

}
