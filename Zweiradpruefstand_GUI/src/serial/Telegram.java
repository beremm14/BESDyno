package serial;

import serial.requests.Request;
import data.RawDatapoint;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import jssc.SerialPortException;
import logging.Logger;
import serial.requests.RequestReset;

/**
 *
 * @author emil
 */
public class Telegram extends RxTxWorker {

    private static Telegram instance;

    private static final Logger LOG = Logger.getLogger(Telegram.class.getName());

    private String response;
    private final Object syncObj = new Object();

    private List<RawDatapoint> list = new LinkedList<>();

    public Telegram() {

    }
    
    public Request resetTarget() {
        synchronized (requestList) {
            final Request request = new RequestReset();
            requestList.add(request);
            requestList.notifyAll();
            return request;
        }
    }
    
    /*public void sendRequest(RequestType request) throws UnsupportedEncodingException, SerialPortException, Exception {
        switch (request) {
            case INIT:
                ConnectPortWorker.getInstance().getPort().writeByte((byte) 'i');
                ConnectPortWorker.getInstance().getPort().writeByte((byte) '\n');
                LOG.info("Request INIT sent");
                break;
            case START:
                ConnectPortWorker.getInstance().getPort().writeByte((byte) 's');
                ConnectPortWorker.getInstance().getPort().writeByte((byte) '\n');
                LOG.info("Request START sent");
                break;
            case ENGINE:
                ConnectPortWorker.getInstance().getPort().writeByte((byte) 'e');
                LOG.info("Request ENGINE sent");
                break;
            case MEASURE:
                ConnectPortWorker.getInstance().getPort().writeByte((byte) 'm');
                LOG.info("Request MEASURE sent");
                break;
            case MEASURENO:
                ConnectPortWorker.getInstance().getPort().writeByte((byte) 'n');
                LOG.info("Request MEASURENO sent");
                break;
            case RESET:
                ConnectPortWorker.getInstance().getPort().writeByte((byte) 'r');
                LOG.info("Request RESET sent");
                break;
            case FINE:
                ConnectPortWorker.getInstance().getPort().writeByte((byte) 'f');
                LOG.info("Request FINE sent");
                break;
            case WARNING:
                ConnectPortWorker.getInstance().getPort().writeByte((byte) 'w');
                LOG.info("Request WARNING sent");
                break;
            case SEVERE:
                ConnectPortWorker.getInstance().getPort().writeByte((byte) 'v');
                LOG.info("Request SEVERE sent");
                break;
            default:
                throw new CommunicationException("Communication Problem...");
        }
    }

    //RESPONSE
    public void initializeCommunication() {
        try {
            synchronized (syncObj) {
                syncObj.wait();
                LOG.fine(response);
            }
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void readEnvironment() {
        try {
            activeWorker = new UARTWorker(RequestType.START);
            activeWorker.execute();
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void readRPM() {
        try {
            activeWorker = new UARTWorker(RequestType.MEASURE);
            activeWorker.execute();
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void readRPMonlyRearWheel() {
        try {
            activeWorker = new UARTWorker(RequestType.MEASURENO);
            activeWorker.execute();
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void resetArduino() {
        try {
            sendRequest(RequestType.RESET);
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void setStatusFine() {
        try {
            sendRequest(RequestType.FINE);
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void setStatusWarning() {
        try {
            sendRequest(RequestType.WARNING);
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void setStatusSevere() {
        try {
            sendRequest(RequestType.SEVERE);
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }
*/
    // Möglichkeit die nicht funktioniert hat...
//    private Future<String> getResponse() {
//        LOG.info("Try to get a Response");
//        return executor.submit(() -> {
//           return Port.getInstance().getPort().readString();
//        });
//    }
//    public String read() throws SerialPortException, ExecutionException, TimeoutException {
//
//        String result = null;
//
//        FutureTask<String> task = new FutureTask<>(new PortReader());
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//
//        try {
//            result = (String) executor.submit(task).get(1000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException | ExecutionException e) {
//            LOG.severe(e);
//        } catch (TimeoutException e) {
//            LOG.severe("Timeout: read();" + e);
//        }
//
//        return result;
//    }
//
//    private class PortReader implements Callable<String>, SerialPortEventListener {
//
//        private String data = null;
//
//        @Override
//        public void serialEvent(SerialPortEvent event) {
//
//            if (event.isRXCHAR() && event.getEventValue() > 0) {
//                try {
//                    data = Port.getInstance().getPort().readString(event.getEventValue());
//                } catch (SerialPortException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        @Override
//        public String call() throws Exception {
//            if (data == null) {
//                Thread.sleep(200);
//            }
//
//            return data;
//        }
//        
//    }
}
