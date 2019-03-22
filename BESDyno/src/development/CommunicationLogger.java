package development;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author emil
 */
public class CommunicationLogger {
    
    private static CommunicationLogger instance;
    
    private final List<LoggedRequest> reqList = new LinkedList<>();
    private final List<LoggedResponse> resList = new LinkedList<>();
    
    public static CommunicationLogger getInstance() {
        if (instance == null) {
            instance = new CommunicationLogger();
        }
        return instance;
    }
    
    public CommunicationLogger() {
    }
    
    public void addReq(LoggedRequest lr) {
        reqList.add(lr);
    }
    
    public void addRes(LoggedResponse lr) {
        resList.add(lr);
    }
    
    public List<LoggedRequest> getReqList() {
        return reqList;
    }
    
    public List<LoggedResponse> getResList() {
        return resList;
    }
    
    public void writeFile(BufferedWriter w) throws IOException {
        w.write("REQUESTS");
        w.newLine();
        for (LoggedRequest lr : reqList) {
            w.write(lr.getReqTime());
            w.write(" : ");
            w.write(lr.getReq());
            w.newLine();
        }
        w.newLine();
        w.write("RESPONSES");
        w.newLine();
        for (LoggedResponse lr : resList) {
            w.write(lr.getResTime());
            w.write(" : ");
            w.write(lr.getRes());
            w.write(" CRC: ");
            w.write(lr.getResCRC() + "<->");
            w.write(lr.getCalcedCRC() + "");
            w.newLine();
        }
    }
    
}
