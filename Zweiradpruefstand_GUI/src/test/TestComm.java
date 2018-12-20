package test;

import data.Environment;
import jssc.SerialPortException;
import logging.Logger;
import serial.Telegram;


/**
 *
 * @author emil
 */
public class TestComm {

    private static final Logger LOG = Logger.getLogger(TestComm.class.getName());
    
    public TestComm() throws SerialPortException, Exception {
        //Telegram.getInstance().readEnvironment();
    }
    
    
    
}
