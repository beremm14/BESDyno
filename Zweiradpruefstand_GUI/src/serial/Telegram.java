package serial;

import serial.requests.Request;
import logging.Logger;
import main.BESDyno;
import serial.requests.*;

/**
 *
 * @author emil
 */
public class Telegram extends RxTxWorker {

    private static final Logger LOG = Logger.getLogger(Telegram.class.getName());

    public Telegram() {

    }
    
    private void devLog(String msg) {
        if (BESDyno.getInstance().isDevMode()) {
            LOG.debug(msg);
        }
    }
    
    public Request init() {
        synchronized (requestList) {
            final Request request = new RequestInit();
            devLog("new: RequestInit();");
            
            request.setStatus(Request.Status.WAITINGTOSEND);
            devLog("Request INIT: WAITING-TO-SEND");
            
            requestList.add(request);
            devLog("Request INIT added to synchronized LinkedList<>():requestList");
            
            requestList.notifyAll();
            devLog("synchronized LinkedList<>():requestList notified");
            
            return request;
        }
    }
    
    public Request start() {
        synchronized (requestList) {
            final Request request = new RequestStart();
            devLog("new: requestStart();");
            
            request.setStatus(Request.Status.WAITINGTOSEND);
            devLog("Request START: WAITING-TO-SEND");
            
            requestList.add(request);
            devLog("Request START added to synchronized LinkedList<>():requestList");
            
            requestList.notifyAll();
            devLog("synchronized LinkedList<>():requestList notified");
            
            return request;
        }
    }
    
    public Request engine() {
        synchronized (requestList) {
            final Request request = new RequestEngine();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            return request;
        }
    }
    
    public Request measure() {
        synchronized (requestList) {
            final Request request = new RequestMeasure();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            return request;
        }
    }
    
    public Request measureno() {
        synchronized (requestList) {
            final Request request = new RequestMeasureno();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            return request;
        }
    }
    
    public Request fine() {
        synchronized (requestList) {
            final Request request = new RequestStatusFine();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            return request;
        }
    }
    
    public Request warning() {
        synchronized (requestList) {
            final Request request = new RequestStatusWarning();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            return request;
        }
    }
    
    public Request severe() {
        synchronized (requestList) {
            final Request request = new RequestStatusSevere();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            return request;
        }
    }
    
    public Request maxProblems() {
        synchronized (requestList) {
            final Request request = new RequestStatusMaxProblems();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            return request;
        }
    }
    
}
