package serial;

import data.Bike;
import data.Config;
import data.Database;
import data.RawDatapoint;
import development.CommunicationLogger;
import development.LoggedResponse;
import java.io.IOException;
import serial.requests.Request;
import java.util.LinkedList;
import java.util.List;
import java.util.TooManyListenersException;
import logging.Logger;
import javax.swing.SwingWorker;
import jssc.SerialPortException;
import main.BESDyno;
import serial.Response.ResponseStatus;
import serial.requests.Request.Status;
import serial.requests.RequestInit;
import serial.requests.RequestVersion;

/**
 *
 * @author emil
 */
public class RxTxWorker extends SwingWorker<Object, Request> {

    private static final Logger LOG = Logger.getLogger(RxTxWorker.class.getName());

    private jssc.SerialPort jsscPort;
    private gnu.io.SerialPort rxtxPort;
    private Object port;

    protected final List<Request> requestList = new LinkedList<>();

    private final Response response = new Response();
    private final StringBuilder continous = new StringBuilder();

    private final CommunicationLogger COMLOG = new CommunicationLogger();

    private final List<String> resList = new LinkedList();
    private final CRC crc = new CRC();

    public void setSerialPort(UARTManager manager) throws SerialPortException, TooManyListenersException {
        this.port = manager.getPort();
        if (manager.getPort() instanceof jssc.SerialPort) {
            this.jsscPort = (jssc.SerialPort) manager.getPort();
            if (manager.getPort() != null) {
                jsscPort.addEventListener((jssc.SerialPortEvent spe) -> {
                    try {
                        handleJSSCPortEvent(spe);
                    } catch (InterruptedException ex) {
                        LOG.severe(ex);
                    }
                });
            }
        } else if (manager.getPort() instanceof gnu.io.SerialPort) {
            this.rxtxPort = (gnu.io.SerialPort) manager.getPort();
            if (manager.getPort() != null) {
                rxtxPort.notifyOnDataAvailable(true);
                rxtxPort.addEventListener((gnu.io.SerialPortEvent spe) -> {
                    handleRXTXPortEvent(spe);
                });
            }
        }
    }

    public void clearReceivedFrames() {
        response.getReceivedFrame().delete(0, response.getReceivedFrame().length());
        LOG.debug("synchronized receivedFrame deleted");
    }

    private void handleJSSCPortEvent(jssc.SerialPortEvent spe) throws InterruptedException {
        if (spe.isRXCHAR()) {
            LOG.debug("SerialPort Event happened!!! :)");

            if (Config.getInstance().isContinous() && BESDyno.getInstance().isListening()) {
                while (true) {
                    try {
                        final byte[] b = jsscPort.readBytes(1);
                        String s = new String(b).trim();
                        if (s.isEmpty()) {
                            break;
                        }
                        if (b == null || b.length == 0) {
                            break;
                        }
                        if (continous.length() == 0) {
                            if (s.contains(":")) {
                                LOG.debug("Continous Frame starts");
                                continous.append(s);
                            }
                        } else {
                            continous.append(s);
                            if (s.contains(";")) {
                                LOG.debug("Continous Frame ends");
                                synchronized (resList) {
                                    resList.add(continous.toString());
                                    resList.notifyAll();
                                    LOG.debug("Continous Frame added to List: " + continous.toString());
                                }
                                continous.delete(0, continous.length());
                                break;
                            }
                        }
                    } catch (SerialPortException ex) {
                        LOG.warning(ex);
                    }
                }

            } else {
                while (true) {
                    try {

                        final byte[] b = jsscPort.readBytes(1);
                        if (b == null || b.length == 0) {
                            break;
                        }
                        String s = new String(b).trim();
                        if (s.isEmpty()) {
                            break;
                        }
                        synchronized (response) {
                            response.getReceivedFrame().append(s);
                            if (s.contains(";")) {
                                response.notifyAll();
                            }
                        }
                    } catch (SerialPortException ex) {
                        LOG.warning(ex);
                    }

                }
            }
        }
    }

    private void handleRXTXPortEvent(gnu.io.SerialPortEvent spe) {

        switch (spe.getEventType()) {
            case gnu.io.SerialPortEvent.DATA_AVAILABLE:
                LOG.debug("SerialPort Event happened!!! :)");

                if (Config.getInstance().isContinous() && BESDyno.getInstance().isListening()) {
                    while (true) {
                        try {
                            final byte b;
                            b = (byte) rxtxPort.getInputStream().read();

                            String s = new String(new byte[]{b}).trim();
                            if (s.isEmpty()) {
                                break;
                            }
                            if (continous.length() == 0) {
                                if (s.contains(":")) {
                                    LOG.debug("Continous Frame starts");
                                    continous.append(s);
                                }
                            } else {
                                continous.append(s);
                                if (s.contains(";")) {
                                    LOG.debug("Continous Frame ends");
                                    synchronized (resList) {
                                        resList.add(continous.toString());
                                        resList.notifyAll();
                                        LOG.debug("Continous Frame added to List: " + continous.toString());
                                    }
                                    continous.delete(0, continous.length());
                                    break;
                                }
                            }
                        } catch (IOException ex) {
                            LOG.warning(ex);
                        }
                    }

                } else {
                    while (true) {
                        try {
                            final byte b;
                            b = (byte) rxtxPort.getInputStream().read();

                            String s = new String(new byte[]{b}).trim();

                            if (s.isEmpty()) {
                                break;
                            }
                            synchronized (response) {
                                response.getReceivedFrame().append(s);
                                if (s.contains(";")) {
                                    response.notifyAll();
                                }
                            }
                        } catch (IOException ex) {
                            LOG.warning(ex);
                        }
                    }
                    break;
                }
        }
    }

    private void handleContinousResponses(String res) {
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
    protected Object doInBackground() throws Exception {
        try {
            LOG.info("RxTxWorker started");

            while (!isCancelled()) {

                if (BESDyno.getInstance().isListening()) {
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

                    handleContinousResponses(res);
                    resList.remove(res);

                } else {

                    synchronized (response.getReceivedFrame()) {
                        response.getReceivedFrame().delete(0, response.getReceivedFrame().length());
                    }

                    Request req = null;
                    synchronized (requestList) {
                        do {
                            for (Request r : requestList) {
                                if (r.getStatus() == Status.WAITINGTOSEND) {
                                    req = r;
                                    LOG.debug("doInBackground: Got Request: " + req.getReqName());
                                    break;
                                } else if (r.getStatus() == Status.WAITINGFORRESPONSE) {
                                    break;
                                }
                            }
                            if (req == null) {
                                requestList.wait();
                            }
                        } while (req == null);
                    }

                    req.setStatus(Status.WAITINGTOSEND);

                    req.sendRequest(port);
                    response.setStartTime();

                    int timeoutMillis;
                    if (req instanceof RequestInit || req instanceof RequestVersion) {
                        timeoutMillis = 5000;
                    } else {
                        timeoutMillis = 1000;
                    }

                    String res;
                    synchronized (response) {
                        do {
                            response.wait(100);
                            LOG.debug("Waits for response: " + (System.currentTimeMillis() - response.getStartTime()) + "ms/" + timeoutMillis + "ms");
                        } while (response.getStartTime() + timeoutMillis > System.currentTimeMillis() && response.getReceivedFrame().length() == 0);

                        if (response.getReceivedFrame().length() > 0 && response.getReceivedFrame().charAt(response.getReceivedFrame().length() - 1) == ';') {
                            response.setReturnValue(ResponseStatus.FINISHED);
                        } else if (response.getStartTime() + timeoutMillis < System.currentTimeMillis()) {
                            LOG.debug("Timeout!");
                            response.setReturnValue(ResponseStatus.TIMEOUT);
                        }

                        if (response.getReturnValue() == ResponseStatus.FINISHED) {
                            res = response.getReceivedFrame().toString();
                            LOG.debug("Response: " + res);
                            response.getReceivedFrame().delete(0, response.getReceivedFrame().length() - 1);
                        } else {
                            res = null;
                        }
                    }

                    if (null != response.getReturnValue()) {
                        switch (response.getReturnValue()) {
                            case FINISHED:
                                req.handleResponse(res);
                                break;
                            case TIMEOUT:
                                LOG.debug("REQUEST-Status set: Timeout!");
                                req.setStatus(Status.TIMEOUT);
                                break;
                            case ERROR:
                                req.setStatus(Status.ERROR);
                                break;
                            default:
                                break;
                        }
                    }

                    publish(req);
                    LOG.debug("Request published");

                    synchronized (requestList) {
                        requestList.remove(req);
                    }

                }
            }
        } catch (Throwable th) {
            LOG.severe(th);
        } finally {
            LOG.info("RxTxWorker ended");
        }
        return null;
    }
}
