package serial;

import logging.Logger;
import javax.swing.SwingWorker;
import jssc.SerialPort;
import jssc.SerialPortException;
import main.BESDyno;
import main.BESDyno.MyTelegram;

/**
 *
 * @author emil
 */
public class DisconnectPortWorker extends SwingWorker<Object, Throwable> {

    private static final Logger LOG = Logger.getLogger(DisconnectPortWorker.class.getName());

    private MyTelegram telegram = BESDyno.getInstance().getTelegram();
    private SerialPort port;

    @Override
    protected Object doInBackground() throws Exception {
        telegram.setSerialPort(null);
        if (!telegram.cancel(true)) {
            LOG.warning("Fehler beim Beenden: SwingWorker -> RxTxWorker -> TelegramWorker -> MyTelegramWorker...");
        }
        return null;
    }

}
