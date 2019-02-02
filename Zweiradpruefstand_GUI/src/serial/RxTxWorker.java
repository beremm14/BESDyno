package serial;

import serial.requests.Request;
import java.util.LinkedList;
import java.util.List;
import logging.Logger;
import javax.swing.SwingWorker;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
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

    private jssc.SerialPort port;
    protected final List<Request> requestList = new LinkedList<>();

    private final Response response = new Response();

    public RxTxWorker() {
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
        } else if (this.port != null) {
            this.port.removeEventListener();
        }
    }

    public void clearReceivedFrames() {
        response.getReceivedFrame().delete(0, response.getReceivedFrame().length());
        LOG.debug("synchronized receivedFrame deleted");
    }

    private void handlePortEvent(SerialPortEvent spe) throws InterruptedException {
        if (spe.isRXCHAR()) {
            LOG.debug("SerialPort Event happened!!! :)");
            while (true) {
                try {
                    final byte[] b = port.readBytes(1);
                    if (b == null || b.length == 0) {
                        break;
                    }
                    String s = new String(b).trim();
                    //String s = port.readString().trim();
                    synchronized (response) {
                        response.getReceivedFrame().append(s);
                        if (";".equals(s)) {
                            response.notifyAll();
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

                    if (response.getReceivedFrame().length() > 0 && response.getReceivedFrame().charAt(response.getReceivedFrame().length()-1) == ';') {
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

                if (null != response.getReturnValue()) switch (response.getReturnValue()) {
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

                publish(req);
                LOG.debug("Request published");

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
