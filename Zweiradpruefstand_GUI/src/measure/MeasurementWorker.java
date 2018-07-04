package measure;

import data.Bike;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;
import serial.Telegram;

/**
 *
 * @author emil
 */
public class MeasurementWorker extends SwingWorker<Void, String> implements PropertyChangeListener {
    
    private Bike bike = Bike.getInstance();
    private Telegram telegram = Telegram.getInstance(); 
    
    private Calculate calc;

    @Override
    protected Void doInBackground() throws Exception {
        telegram.readEnvData();
        telegram.readRpmData();
        telegram.readBikeTemp();
        
        
        
        return null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
