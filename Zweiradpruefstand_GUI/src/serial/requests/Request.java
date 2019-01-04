package serial.requests;

import java.util.zip.CRC32;
import jssc.SerialPortException;
import serial.CommunicationException;

/**
 *
 * @author emil
 */
public abstract class Request {

    public static enum Status {
        WAITINGTOSEND, WAITINGFORRESPONSE, DONE, ERROR
    };
    
    public static enum Variety {
        INIT, START, MEASURE, MEASURENO, ENGINE, FINE, WARNING, SEVERE, MAXPROBLEMS
    };

    protected Status status;

    public Request() {
        status = Status.WAITINGTOSEND;
    }
    
    public abstract void sendRequest(jssc.SerialPort port) throws CommunicationException, SerialPortException;
    
    public abstract String getReqMessage();
    public abstract String getReqName();
    public abstract Variety getVariety();
    public abstract void handleResponse(String res);
    public abstract String getResponse();

    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }

    protected boolean checkCRC(String res) {
        CRC32 crc = new CRC32();
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        
        String toCheck[] = response.split(">");
        
        byte[] b = toCheck[0].trim().getBytes();
        crc.update(b);
        long checksum = crc.getValue();
        
        return checksum == Long.parseLong(toCheck[1]);
    }
    
    protected String removeCRC(String s) {
        String[]value = s.split(">");
        String rv = value[0].replaceAll(":", "");
        return rv;
    }
    
    protected long calcCRC(String res) {
        CRC32 crc = new CRC32();
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        
        String toCheck[] = response.split(">");
        
        byte[]b = toCheck[0].trim().getBytes();
        crc.update(b);
        return crc.getValue();
    }
    
    protected long getSentCRC(String res) {
        String response = res.replaceAll(":", "");
        response = response.replaceAll(";", "");
        String[]value = response.split(">");
        return Long.parseLong(value[1]);
    }

}
