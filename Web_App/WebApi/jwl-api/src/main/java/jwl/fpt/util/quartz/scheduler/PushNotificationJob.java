package jwl.fpt.util.quartz.scheduler;

import jwl.fpt.service.IBookBorrowService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by HaVH on 4/6/17.
 */
@DisallowConcurrentExecution
public class PushNotificationJob implements Job {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IBookBorrowService bookBorrowService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Calendar cal = Calendar.getInstance();
        Date fireTime = jobExecutionContext.getFireTime();
        cal.setTime(fireTime);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        if (hours >= 8) {
            logger.info("Job ** {} ** fired @ {}", jobExecutionContext.getJobDetail().getKey().getName(), jobExecutionContext.getFireTime());
            bookBorrowService.sendNotificationForLateDeadline();
            logger.info("Next job scheduled @ {}", jobExecutionContext.getNextFireTime());
        }
    }
}
