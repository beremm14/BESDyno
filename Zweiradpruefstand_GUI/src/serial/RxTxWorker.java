package serial;

import serial.requests.Request;
import java.util.LinkedList;
import java.util.List;
import logging.Logger;
import javax.swing.SwingWorker;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
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

    public void setSerialPort(jssc.SerialPort port) throws SerialPortException {
        this.port = port;
        if (port != null) {
            port.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(SerialPortEvent spe) {
                    handlePortEvent(spe);
                }
            });
        }
    }

    private void handlePortEvent(SerialPortEvent spe) {
        if (spe.isRXCHAR()) {
            while (true) {
                try {
                    final byte[] b = port.readBytes(1);
                    if (b == null || b.length == 0) {
                        break;
                    }
                    String s = new String(b);
                    synchronized (receivedFrame) {
                        receivedFrame.append(s);
                        if (s == ";") {

                            receivedFrame.notifyAll();
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
                req.setStatus(Status.WAITINGFORRESPONSE);
                publish(req);
                Thread.sleep(1000);
                synchronized (receivedFrame) {
                    receivedFrame.delete(0, receivedFrame.length() - 1);
                }
                req.sendRequest(port);
                publish(req);

                String res;
                synchronized (receivedFrame) {
                    while (receivedFrame.length() == 0 || receivedFrame.charAt(receivedFrame.length()-1) != ';') {
                        receivedFrame.wait();
                    }
                    res = receivedFrame.toString();
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
