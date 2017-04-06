package jwl.fpt.util.quartz.scheduler;

import jwl.fpt.util.quartz.config.AutoWiringSpringBeanJobFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by HaVH on 3/19/17.
 */
@Configuration
public class QrtzScheduler {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        logger.info("Hello world from Quartz...");
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        logger.debug("Configuring Job factory");

        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public Scheduler scheduler() throws SchedulerException, IOException {

        StdSchedulerFactory factory = new StdSchedulerFactory();
        factory.initialize(new ClassPathResource("quartz.properties").getInputStream());

        JobDetail jobCheckout = newJob()
                .ofType(AutoCheckoutJob.class)
                .storeDurably()
                .withIdentity(JobKey.jobKey("Check_Out_Detail"))
                .withDescription("Invoke Check Out Job service...").build();

        JobDetail jobCheckDeadline = newJob()
                .ofType(CheckDeadlineJob.class)
                .storeDurably()
                .withIdentity(JobKey.jobKey("Check_Deadline_Detail"))
                .withDescription("Invoke Check Deadline Job service...").build();

        JobDetail jobPushNotifcation = newJob()
                .ofType(PushNotificationJob.class)
                .storeDurably()
                .withIdentity(JobKey.jobKey("Push_Noti_Detail"))
                .withDescription("Invoke Push Noti Job service...").build();

        Trigger checkoutTrigger = newTrigger()
                .withIdentity(TriggerKey.triggerKey("Check_Out_Trigger"))
                .withDescription("check out trigger")
                .startNow()
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(19, 0)).build();

        Trigger deadlineTrigger = newTrigger()
                .withIdentity(TriggerKey.triggerKey("Deadline_Trigger"))
                .withDescription("deadline trigger")
                .startNow()
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(2, 0)).build();

        Trigger pushNotiTrigger = newTrigger()
                .withIdentity(TriggerKey.triggerKey("Push_Noti_Trigger"))
                .withDescription("push noti trigger")
                .startNow()
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(8, 0)).build();

        logger.debug("Getting a handle to the Scheduler");
        Scheduler scheduler = factory.getScheduler();
        scheduler.setJobFactory(springBeanJobFactory());
        scheduler.scheduleJob(jobCheckout, checkoutTrigger);
        scheduler.scheduleJob(jobCheckDeadline, deadlineTrigger);
        scheduler.scheduleJob(jobPushNotifcation, pushNotiTrigger);

        logger.debug("Starting Scheduler threads");
        scheduler.start();
        return scheduler;
    }

//    @Bean
//    public JobDetail jobDetail() {
//
//        return newJob().ofType(CheckDeadlineJob.class).storeDurably().withIdentity(JobKey.jobKey("Qrtz_Job_Detail")).withDescription("Invoke Sample Job service...").build();
//    }
//
//    @Bean
//    public Trigger trigger(JobDetail job) {
//
//        int frequencyInSec = 10;
//        logger.info("Configuring trigger to fire every {} seconds", frequencyInSec);
//
//        return newTrigger()
//                .forJob(job)
//                .withIdentity(TriggerKey.triggerKey("Qrtz_Trigger"))
//                .withDescription("Sample trigger")
//                .startNow()
//                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(8, 0)).build();
//    }
}
