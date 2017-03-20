package jwl.fpt.util.quartz.scheduler;

import jwl.fpt.service.imp.BookBorrowService.BookBorrowService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


/**
 * Created by HaVH on 3/18/17.
 */
public class CheckDeadlineJob implements Job{
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    BookBorrowService bookBorrowService;

    public CheckDeadlineJob() {
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //Todo: check deadline of book here
        logger.info("Job ** {} ** fired @ {}", jobExecutionContext.getJobDetail().getKey().getName(), jobExecutionContext.getFireTime());
        bookBorrowService.checkBorrowingBookCopyDeadline();
        logger.info("Next job scheduled @ {}", jobExecutionContext.getNextFireTime());

    }


//    public boolean checkBorrowingBookCopyDeadline()
}