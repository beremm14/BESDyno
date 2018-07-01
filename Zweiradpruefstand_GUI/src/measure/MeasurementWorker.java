package measure;

import data.Bike;
import javax.swing.SwingWorker;
import serial.Telegram;

/**
 *
 * @author emil
 */
public class MeasurementWorker extends SwingWorker<Telegram, String> {
    
    private Bike bike = Bike.getInstance();
    private Telegram telegram = Telegram.getInstance();    

    @Override
    protected Telegram doInBackground() throws Exception {
        
        telegram.readEnvData();
        //do {
            telegram.readRpmData();
            
        //}
        
        return telegram;
    }
    
}
