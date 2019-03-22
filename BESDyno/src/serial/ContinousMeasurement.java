package serial;

import data.Bike;
import data.Database;
import data.RawDatapoint;
import development.CommunicationLogger;
import development.LoggedResponse;
import java.util.LinkedList;
import java.util.List;
import logging.Logger;

/**
 *
 * @author emil
 */
public class ContinousMeasurement implements Runnable {

    private static final Logger LOG = Logger.getLogger(ContinousMeasurement.class.getName());
    private final CommunicationLogger COMLOG = new CommunicationLogger();

    private final List<String> resList = new LinkedList();
    private final CRC crc = new CRC();
    
    private void handleResponse(String res) {
        COMLOG.addRes(new LoggedResponse(crc.removeCRC(res), crc.getSentCRC(res), crc.calcCRC(res)));

        res = res.replaceAll(":", "");
        res = res.replaceAll(";", "");
        String[] values;
        try {
            values = res.split("#");
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.severe(ex);
            return;
        }

        if (Bike.getInstance().isMeasTemp()) {

            RawDatapoint rdp;
            synchronized (Database.getInstance().getRawList()) {
                try {
                    values[4] = crc.removeCRC(values[4]);
                    rdp = new RawDatapoint(values[0], values[1], values[4]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                    LOG.severe(ex);
                    return;
                }
                if (rdp.getTime() > 0 && crc.checkCRC(res)) {
                    Database.getInstance().addRawDP(rdp);
                    Database.getInstance().addTemperatures(values[2], values[3]);
                } else {
                    LOG.warning("CONTINOUS WITHIN TEMP ERROR: " + res);
                }
            }
        } else {

            RawDatapoint rdp;
            synchronized (Database.getInstance().getRawList()) {
                try {
                    values[2] = crc.removeCRC(values[4]);
                    rdp = new RawDatapoint(values[0], values[1], values[2]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                    LOG.severe(ex);
                    return;
                }
                if (rdp.getTime() > 0 && crc.checkCRC(res)) {
                    Database.getInstance().addRawDP(rdp);
                } else {
                    LOG.warning("CONTINOUS ERROR: " + res);
                }
            }
        }

    }
    
    @Override
    public void run() {
        while (true) {
            synchronized (resList) {
                for (String res : resList) {
                    handleResponse(res);
                    resList.remove(res);
                }
            }
        }
    }

    public List<String> getResList() {
        return resList;
    }

    public boolean add(String res) {
        return resList.add(res);
    }

}
