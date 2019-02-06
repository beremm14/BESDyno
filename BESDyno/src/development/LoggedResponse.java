package development;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author emil
 */
public class LoggedResponse {
    
    private final String res;
    private final String resTime;
    private final long resCRC;
    private final long calcedCRC;

    public LoggedResponse(String res, long resCRC, long calcedCRC) {
        Date date = Calendar.getInstance().getTime();
        //Year-Month-Day Hour:Minutes:Seconds.Milliseconds
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        this.resTime = df.format(date);
        this.res = res;
        this.resCRC = resCRC;
        this.calcedCRC = calcedCRC;
    }

    public String getRes() {
        return res;
    }

    public String getResTime() {
        return resTime;
    }

    public long getResCRC() {
        return resCRC;
    }

    public long getCalcedCRC() {
        return calcedCRC;
    }

}
