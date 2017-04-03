package jwl.fpt.util;

import java.sql.Date;
import static jwl.fpt.util.Constant.*;

/**
 * Created by Entaard on 2/1/17.
 */
public class Helper {
    static public Date getDateAfter(Date startDate, int dayInterval) {
        long milisec = startDate.getTime() + (long) dayInterval * MILISECOND_PER_DAYS;
        return new Date(milisec);
    }

    static public int getDaysInterval(Date startDate, Date endDate) {
        long milisec = startDate.getTime() - endDate.getTime();
        int daysInterval = (int) (milisec / MILISECOND_PER_DAYS);
        return daysInterval;
    }
}
