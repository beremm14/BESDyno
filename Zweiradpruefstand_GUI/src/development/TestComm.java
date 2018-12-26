package development;

import jssc.SerialPortException;
import logging.Logger;
import main.BESDyno;
import main.BESDyno.MyTelegram;


/**
 *
 * @author emil
 */
public class TestComm {

    private static final Logger LOG = Logger.getLogger(TestComm.class.getName());
    
    private MyTelegram telegram = BESDyno.getInstance().getTelegram();
    
    public TestComm() throws SerialPortException, Exception {
        LOG.debug("Test communication...");
        BESDyno.getInstance().addPendingRequest(telegram.init());
    }

}
