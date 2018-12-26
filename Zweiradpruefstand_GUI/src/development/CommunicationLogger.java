package development;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author emil
 */
public class CommunicationLogger {
    
    private static CommunicationLogger instance;
    
    private boolean enableLogging;
    private final List<String> reqList = new LinkedList<>();
    private final List<String> resList = new LinkedList<>();
    
    private final Date date = Calendar.getInstance().getTime();
    //Year-Month-Day Hour:Minutes:Seconds.Milliseconds
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    
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
        reqList.add(df.format(date) + ": " + req);
    }
    
    public void addRes(String res) {
        resList.add(df.format(date) + ": " + res);
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
