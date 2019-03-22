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
public class MeasurementListener extends Thread {
    
    private static MeasurementListener instance;

    private static final Logger LOG = Logger.getLogger(MeasurementListener.class.getName());
    private final CommunicationLogger COMLOG = new CommunicationLogger();

    private final List<String> resList = new LinkedList();
    private final CRC crc = new CRC();

    private boolean stopped = false;
    
    public static MeasurementListener getInstance() {
        if (instance == null) {
            instance = new MeasurementListener();
        }
        return instance;
    }

    private MeasurementListener() {
    }

    private void handleResponse(String res) {
        COMLOG.addRes(new LoggedResponse(crc.removeCRC(res), crc.getSentCRC(res), crc.calcCRC(res)));
        LOG.debug("Continous Response: " + res);

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
                    Database.getInstance().getRawList().notifyAll();
                } else {
                    LOG.warning("CONTINOUS WITHIN TEMP ERROR: " + res);
                }
            }
        } else {

            RawDatapoint rdp;
            synchronized (Database.getInstance().getRawList()) {
                try {
                    values[2] = crc.removeCRC(values[2]);
                    rdp = new RawDatapoint(values[0], values[1], values[2]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                    LOG.severe(ex);
                    return;
                }
                if (rdp.getTime() > 0 && crc.checkCRC(res)) {
                    LOG.debug("Listener pushed RDP into Database");
                    Database.getInstance().addRawDP(rdp);
                    Database.getInstance().getRawList().notifyAll();
                } else {
                    LOG.warning("CONTINOUS ERROR: " + res);
                }
            }
        }

    }

    @Override
    public void run() {
        LOG.info("Continous Measurement Thread started...");
        while (!stopped) {
            String res = null;
            do {
                try {
                    synchronized (resList) {
                        for (String r : resList) {
                            res = r;
                            break;
                        }

                        if (res == null) {
                            resList.wait();
                        }
                    }
                } catch (Exception ex) {
                    LOG.warning(ex);
                }
            } while (res == null);

            handleResponse(res);
            resList.remove(res);
        }
        LOG.warning("Continous Measurement Thread ended...");
    }

    public void stopListening() {
        stopped = true;
    }

    public List<String> getResList() {
        return resList;
    }

    public boolean add(String res) {
        return resList.add(res);
    }

}
