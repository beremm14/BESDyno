package serial;

import serial.requests.Request;
import java.util.LinkedList;
import java.util.List;
import logging.Logger;
import javax.swing.SwingWorker;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import main.BESDyno;
import serial.requests.Request.Status;

/**
 *
 * @author emil
 */
public class RxTxWorker extends SwingWorker<Object, Request> {

    private static final Logger LOG = Logger.getLogger(RxTxWorker.class.getName());

    private jssc.SerialPort port;
    protected final List<Request> requestList = new LinkedList<>();

    private final StringBuilder receivedFrame = new StringBuilder(1024);

    public RxTxWorker() {
    }
    
    private void devLog(String msg) {
        if (BESDyno.getInstance().isDevMode()) {
            LOG.debug(msg);
        }
    }

    public void setSerialPort(jssc.SerialPort port) throws SerialPortException {
        this.port = port;
        if (port != null) {
            port.addEventListener((SerialPortEvent spe) -> {
                try {
                    handlePortEvent(spe);
                } catch (InterruptedException ex) {
                    LOG.severe(ex);
                }
            });
        }
    }

    private void handlePortEvent(SerialPortEvent spe) throws InterruptedException {
        if (spe.isRXCHAR()) {
            devLog("SerialPort Event happened!!! :)");
            while (true) {
                try {
                    final byte[] b = port.readBytes(1);
                    if (b == null || b.length == 0) {
                        break;
                    }
                    String s = new String(b).trim();
                    //String s = port.readString().trim();
                    devLog("Response-String: " + s);
                    synchronized (receivedFrame) {
                        receivedFrame.append(s);
                        if (";".equals(s)) {
                            receivedFrame.notifyAll();
                            devLog("Response-String built -> synchronized receivedFrame notified");
                        }
                    }
                } catch (SerialPortException ex) {
                    LOG.warning(ex);
                }

            }
        }
    }

    @Override
    protected Object doInBackground() throws Exception {
        try {
            LOG.info("RxTxWorker started");
            while (!isCancelled()) {
                Request req = null;
                synchronized (requestList) {
                    do {
                        for (Request r : requestList) {
                            if (r.getStatus() == Status.WAITINGTOSEND) {
                                req = r;
                                devLog("doInBackground: Got Request: " + req.getReqName());
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
                devLog("Request " + req.getReqName() + " WAITING-TO-SEND");
                
                devLog("Request " + req.getReqName() + " on the way...");
                req.sendRequest(port);
                devLog("Request " + req.getReqName() + ": sending completed");
                
                //publish(req);
                //devLog("Request " + req.getReqName() + " published");
                
                synchronized (receivedFrame) {
                    receivedFrame.delete(0, receivedFrame.length());
                    devLog("synchronized receivedFrame deleted");
                }
                
                //publish(req);

                String res;
                synchronized (receivedFrame) {
                    while (receivedFrame.length() == 0 || receivedFrame.charAt(receivedFrame.length()-1) != ';') {
                        receivedFrame.wait();
                    }
                    res = receivedFrame.toString();
                    devLog("Response: toString(): " + res);
                    receivedFrame.delete(0, receivedFrame.length()-1);
                }
                
                req.handleResponse(res);
                
                publish(req);
                

                synchronized (requestList) {
                    requestList.remove(req);
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
