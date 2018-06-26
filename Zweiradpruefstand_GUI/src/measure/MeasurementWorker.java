package measure;

import data.Bike;
import javax.swing.SwingWorker;
import serial.Telegram;

/**
 *
 * @author emil
 */
public class MeasurementWorker extends SwingWorker<Void, String> {
    
    private Bike bike = new Bike();
    private Telegram telegram = new Telegram();    

    @Override
    protected Void doInBackground() throws Exception {
        
        telegram.readEnvData();
        //do {
            telegram.readRpmData();
            
        //}
        
        return null;
    }
    
}
