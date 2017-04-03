package jwl.fpt.util.quartz.scheduler;

import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.service.imp.BookBorrowService.BookBorrowService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.util.Date;


/**
 * Created by HaVH on 3/18/17.
 */
public class CheckDeadlineJob implements Job{
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IBookBorrowService bookBorrowService;

    public CheckDeadlineJob() {
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Job ** {} ** fired @ {}", jobExecutionContext.getJobDetail().getKey().getName(), jobExecutionContext.getFireTime());
        try {
            bookBorrowService.checkBorrowingBookCopyDeadline();
        } catch (UnsupportedEncodingException | NullPointerException e) {
            e.printStackTrace();
        }
        logger.info("Next job scheduled @ {}", jobExecutionContext.getNextFireTime());

    }


//    public boolean checkBorrowingBookCopyDeadline()
}
