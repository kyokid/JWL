package jwl.fpt.util.quartz.scheduler;

import jwl.fpt.service.imp.UserService.UserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by HaVH on 4/10/17.
 */
public class AutoCheckoutJob implements Job {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Job ** {} ** fired @ {}", jobExecutionContext.getJobDetail().getKey().getName(), jobExecutionContext.getFireTime());

        userService.autoCheckOutUser();

        logger.info("Next job scheduled @ {}", jobExecutionContext.getNextFireTime());
    }
}
