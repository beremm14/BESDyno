package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import measure.Calculate;

/**
 *
 * @author emil
 */
public class CSV {
    
    private final Database data = Database.getInstance();
    private final File file;
    private final boolean filter;
    private final List<RawDatapoint> rdpList = new LinkedList<>();

    public CSV(File file) {
        this.file = file;
        filter = !file.getAbsolutePath().contains("Filtered");
    }
    
    public void readCSVFiles() throws FileNotFoundException, IOException {
        String line;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            while((line = r.readLine()) != null) {
                String values[] = line.split(",");
                RawDatapoint rdp = new RawDatapoint(values[0], values[1], values[2]);
                rdpList.add(rdp);
            } 
        }
    }
    
    public void calcChart() {
        data.clearLists();
        Calculate calc = new Calculate();
        calc.calcPower(rdpList, calc.calcPreList(rdpList), filter);
    }
    
}
