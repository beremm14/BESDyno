package serial;

import javax.swing.SwingWorker;
import logging.Logger;
import main.BESDyno;
import main.BESDyno.OS;

/**
 *
 * @author emil
 */
public class ConnectPortWorker extends SwingWorker<RxTxManager, Object> {

    private static final Logger LOG = Logger.getLogger(ConnectPortWorker.class.getName());

    private final String port;

    public ConnectPortWorker(String port) {
        this.port = port;
    }

    @Override
    protected RxTxManager doInBackground() throws Exception {
        RxTxManager manager = new RxTxManager();
        manager.portFactory(BESDyno.getInstance().getOs() == OS.MACOS, port);
        if (manager.getPort() instanceof gnu.io.SerialPort) {
            LOG.info("macOS: Uses RXTX Library");
        } else if (manager.getPort() instanceof jssc.SerialPort) {
            LOG.info("Uses JSSC Library");
        }
        manager.openPort();
        LOG.info("Connected");
        manager.setParams(57600, 8, 1, 0);
        LOG.info("Params set: Baudrate: 57600, Databits: 8, Stopbits: 1, Parity: NONE");
        return manager;
    }

}
