package serial;

import data.RawDatapoint;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import jssc.SerialPortException;
import logging.Logger;

/**
 *
 * @author emil
 */
public class Telegram {

    private static Telegram instance = null;

    private static final Logger LOG = Logger.getLogger(Telegram.class.getName());
    private final jssc.SerialPort port = Port.getInstance().getPort();

    private SwingWorker activeWorker;

    public static Telegram getInstance() {
        if (instance == null) {
            instance = new Telegram();
        }
        return instance;
    }

    private Telegram() {

    }

    private String response;
    private final Object syncObj = new Object();

    private List<RawDatapoint> list = new LinkedList<>();

    public void sendRequest(Request request) throws UnsupportedEncodingException, SerialPortException, Exception {
        switch (request) {
            case INIT:
                Port.getInstance().getPort().writeByte((byte) 'i');
                Port.getInstance().getPort().writeByte((byte) '\n');
                LOG.info("Request INIT sent");
                break;
            case START:
                Port.getInstance().getPort().writeByte((byte) 's');
                Port.getInstance().getPort().writeByte((byte) '\n');
                LOG.info("Request START sent");
                break;
            case ENGINE:
                Port.getInstance().getPort().writeByte((byte) 'e');
                LOG.info("Request ENGINE sent");
                break;
            case MEASURE:
                Port.getInstance().getPort().writeByte((byte) 'm');
                LOG.info("Request MEASURE sent");
                break;
            case MEASURENO:
                Port.getInstance().getPort().writeByte((byte) 'n');
                LOG.info("Request MEASURENO sent");
                break;
            case RESET:
                Port.getInstance().getPort().writeByte((byte) 'r');
                LOG.info("Request RESET sent");
                break;
            case FINE:
                Port.getInstance().getPort().writeByte((byte) 'f');
                LOG.info("Request FINE sent");
                break;
            case WARNING:
                Port.getInstance().getPort().writeByte((byte) 'w');
                LOG.info("Request WARNING sent");
                break;
            case SEVERE:
                Port.getInstance().getPort().writeByte((byte) 'v');
                LOG.info("Request SEVERE sent");
                break;
            default:
                throw new CommunicationException("Communication Problem...");
        }
    }

    //RESPONSE
    public void initializeCommunication() {
        try {
            activeWorker = new UARTWorker(Request.INIT);
            activeWorker.execute();
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
            activeWorker = new UARTWorker(Request.START);
            activeWorker.execute();
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void readRPM() {
        try {
            activeWorker = new UARTWorker(Request.MEASURE);
            activeWorker.execute();
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void readRPMonlyRearWheel() {
        try {
            activeWorker = new UARTWorker(Request.MEASURENO);
            activeWorker.execute();
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void resetArduino() {
        try {
            sendRequest(Request.RESET);
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void setStatusFine() {
        try {
            sendRequest(Request.FINE);
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void setStatusWarning() {
        try {
            sendRequest(Request.WARNING);
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void setStatusSevere() {
        try {
            sendRequest(Request.SEVERE);
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }
    
    private class UARTWorker extends RxTxWorker {
        
        public UARTWorker(Request request) {
            super(request);
        }

        @Override
        protected void done() {
            try {
                synchronized (syncObj) {
                    response = get();
                    syncObj.notify();
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOG.severe(ex);
            } finally {
                activeWorker = null;
            }
        }  
    }
    
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
