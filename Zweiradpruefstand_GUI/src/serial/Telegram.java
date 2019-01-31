package serial;

import serial.requests.Request;
import serial.requests.*;

/**
 *
 * @author emil
 */
public class Telegram extends RxTxWorker {

    public Telegram() {

    }
    
    public Request retryRequest(Request request) {
        synchronized (requestList) {
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(false);
            return request;
        }
    }
    
    public Request init() {
        synchronized (requestList) {
            final Request request = new RequestInit();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(true);
            return request;
        }
    }
    
    public Request start() {
        synchronized (requestList) {
            final Request request = new RequestStart();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(true);
            return request;
        }
    }
    
    public Request engine() {
        synchronized (requestList) {
            final Request request = new RequestEngine();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(true);
            return request;
        }
    }
    
    public Request measure() {
        synchronized (requestList) {
            final Request request = new RequestMeasure();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(false);
            return request;
        }
    }
    
    public Request measureno() {
        synchronized (requestList) {
            final Request request = new RequestMeasureno();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(false);
            return request;
        }
    }
    
    public Request fine() {
        synchronized (requestList) {
            final Request request = new RequestStatusFine();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(false);
            return request;
        }
    }
    
    public Request warning() {
        synchronized (requestList) {
            final Request request = new RequestStatusWarning();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(false);
            return request;
        }
    }
    
    public Request severe() {
        synchronized (requestList) {
            final Request request = new RequestStatusSevere();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(false);
            return request;
        }
    }
    
    public Request maxProblems() {
        synchronized (requestList) {
            final Request request = new RequestStatusMaxProblems();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(false);
            return request;
        }
    }
    
    public Request kill() {
        synchronized (requestList) {
            final Request request = new RequestKill();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(false);
            return request;
        }
    }
    
    public Request version() {
        synchronized (requestList) {
            final Request request = new RequestVersion();
            request.setStatus(Request.Status.WAITINGTOSEND);
            requestList.add(request);
            requestList.notifyAll();
            request.setTimeOutComp(true);
            return request;
        }
    }
    
}
