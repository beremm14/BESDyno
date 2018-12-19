package serial;

import java.util.concurrent.TimeUnit;
import javax.swing.SwingWorker;

/**
 *
 * @author emil
 */
public class RxTxWorker extends SwingWorker<String, String> {
    
    private Request request;
    
    public RxTxWorker(Request request) {
        this.request = request;
    }
    
    @Override
    protected String doInBackground() throws Exception {
        Telegram.getInstance().sendRequest(request);
        TimeUnit.MILLISECONDS.sleep(10);
        return Port.getInstance().getPort().readString();
        
    }
    
}
