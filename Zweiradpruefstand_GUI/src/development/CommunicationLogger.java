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
    
    private boolean enableLogging;
    private List<String> reqList = new LinkedList<>();
    private List<String> resList = new LinkedList<>();
    
    public static CommunicationLogger getInstance() {
        if (instance == null) {
            instance = new CommunicationLogger();
        }
        return instance;
    }
    
    private CommunicationLogger() {
    }

    public boolean isEnabled() {
        return enableLogging;
    }

    public void setCommLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }
    
    public void addReq(String req) {
        reqList.add(req);
    }
    
    public void addRes(String res) {
        resList.add(res);
    }

    public List<String> getReqList() {
        return reqList;
    }

    public List<String> getResList() {
        return resList;
    }
    
    public void writeFile(BufferedWriter w) throws IOException {
        w.write("REQUESTS");
        w.newLine();
        for (String s : reqList) {
            w.write(s);
            w.newLine();
        }
        w.newLine();
        w.write("RESPONSES");
        w.newLine();
        for(String s : resList) {
            w.write(s);
            w.newLine();
        }
    }

}
