package main;

import gui.BESDyno;
import javax.swing.UnsupportedLookAndFeelException;
import logging.LogBackgroundHandler;
import logging.LogOutputStreamHandler;
import logging.Logger;

/**
 *
 * @author emil
 */
public class Main {

    static {
        //System.setProperty("logging.Logger.printStackTrace", "");
        System.setProperty("logging.LogOutputStreamHandler.showRecordHashcode", "false");
        //System.setProperty("logging.Logger.printAll", "");
        //System.setProperty("logging.LogRecordDataFormattedText.Terminal","NETBEANS");
        System.setProperty("logging.LogRecordDataFormattedText.Terminal", "LINUX");
        System.setProperty("logging.Logger.Level", "INFO");
        //System.setProperty("Test1.Logger.Level", "ALL");
        System.setProperty("test.Test.Logger.Level", "FINER");
        System.setProperty("test.*.Logger.Level", "FINE");
        //System.setProperty("test.*.Logger.Handlers", "test.MyHandler");
        //System.setProperty("test.*.Logger.Filter", "test.MyFilter");
        //System.setProperty("logging.LogOutputStreamHandler.colorize", "false");

        LOG = Logger.getLogger(Main.class.getName());
        LOGP = Logger.getParentLogger();
    }

    private static final Logger LOG;
    private static final Logger LOGP;

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        //LOG.setLevel((Level)null);
        LOGP.addHandler(new LogBackgroundHandler(new LogOutputStreamHandler(System.out)));
        //LOG.setLevel(Level.INFO);
        LOG.info("Start of Application");
        BESDyno.main(args);
    }

}
