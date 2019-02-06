package development;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author emil
 */
public class LoggedRequest {
    
    private final String req;
    private final String reqTime;

    public LoggedRequest(String req) {
        Date date = Calendar.getInstance().getTime();
        //Year-Month-Day Hour:Minutes:Seconds.Milliseconds
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        this.reqTime = df.format(date);
        this.req = req;
    }

    public String getReq() {
        return req;
    }

    public String getReqTime() {
        return reqTime;
    }

}
