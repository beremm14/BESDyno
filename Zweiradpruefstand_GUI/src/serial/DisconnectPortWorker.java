package serial;

import logging.Logger;
import javax.swing.SwingWorker;
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

    @Override
    protected Object doInBackground() throws Exception {
        try {
            telegram.setSerialPort(null);
            if (!telegram.cancel(true)) {
                LOG.warning("Fehler beim Beenden: SwingWorker -> RxTxWorker -> TelegramWorker -> MyTelegramWorker...");
                return null;
            }
        } catch (Exception ex) {
            publish(ex);
        } finally {
            try {
                BESDyno.getInstance().setConnection(false);
                BESDyno.getInstance().getPort().closePort();
                BESDyno.getInstance().setPort(null);
            } catch (Throwable th) {
                publish(th);
            }
            try {
                telegram.setSerialPort(null);
            } catch (SerialPortException ex) {
                publish(ex);
            }
        }
        return null;
    }
}
