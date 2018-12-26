package development;

import data.Environment;
import jssc.SerialPortException;
import logging.Logger;
import main.BESDyno;
import main.BESDyno.MyTelegram;
import serial.Telegram;


/**
 *
 * @author emil
 */
public class TestComm {

    private static final Logger LOG = Logger.getLogger(TestComm.class.getName());
    
    private MyTelegram telegram = BESDyno.getInstance().getTelegram();
    
    public TestComm() throws SerialPortException, Exception {
        BESDyno.getInstance().addPendingRequest(telegram.start());
    }
    
    
    
}
