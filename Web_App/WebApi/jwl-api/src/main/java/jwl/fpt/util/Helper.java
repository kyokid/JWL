package jwl.fpt.util;

import java.sql.Date;

/**
 * Created by Entaard on 2/1/17.
 */
public class Helper {
    static public Date GetDateAfter(Date startDate, int dayInterval) {
        long milisec = startDate.getTime() + (long) dayInterval * 24 * 60 * 60 * 1000;
        return new Date(milisec);
    }
}
