package jwl.fpt.util.quartz.scheduler;

import jwl.fpt.util.quartz.config.AutoWiringSpringBeanJobFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.listeners.JobChainingJobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by HaVH on 3/19/17.
 */
@Configuration
@PropertySource("classpath:values.properties")
public class QrtzScheduler {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${checkout.hour}")
    private Integer checkoutHour;

    @Value("${checkout.minute}")
    private Integer checkoutMinute;

    @Value("${checkDeadline.hour}")
    private Integer checkDeadlineHour;

    @Value("${checkDeadline.minute}")
    private Integer checkDeadlineMinute;

    @Value("${pushNoti.hour}")
    private Integer pushNotiHour;

    @Value("${pushNoti.minute}")
    private Integer pushNotiMinute;


    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        logger.info("Hello world from Quartz...");
    }


    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        return c;
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
                .withIdentity(JobKey.jobKey("Check_Out_Detail"))
                .withDescription("Invoke Check Out Job service...").build();

        JobDetail jobCheckDeadline = newJob()
                .ofType(CheckDeadlineJob.class)
                .withIdentity(JobKey.jobKey("Check_Deadline_Detail"))
                .withDescription("Invoke Check Deadline Job service...").build();

        JobDetail jobPushNotifcation = newJob()
                .ofType(PushNotificationJob.class)
                .withIdentity(JobKey.jobKey("Push_Noti_Detail"))
                .withDescription("Invoke Push Noti Job service...").build();

        Trigger checkoutTrigger = newTrigger()
                .withIdentity(TriggerKey.triggerKey("Check_Out_Trigger"))
                .withDescription("check out trigger")
                .startNow()
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(checkoutHour, checkoutMinute)).build();

        Trigger deadlineTrigger = newTrigger()
                .withIdentity(TriggerKey.triggerKey("Deadline_Trigger"))
                .withDescription("deadline trigger")
                .withPriority(9)
                .startNow()
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(checkDeadlineHour, checkDeadlineMinute))
                .build();

        Trigger pushNotiTrigger = newTrigger()
                .withIdentity(TriggerKey.triggerKey("Push_Noti_Trigger"))
                .withDescription("push noti trigger")
                .startNow()
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(pushNotiHour, pushNotiMinute))
                .build();

        logger.debug("Getting a handle to the Scheduler");

        JobChainingJobListener jobChainingJobListener = new JobChainingJobListener("myChainListener");
        jobChainingJobListener.addJobChainLink(jobCheckDeadline.getKey(), jobPushNotifcation.getKey());


        Scheduler scheduler = factory.getScheduler();
        scheduler.setJobFactory(springBeanJobFactory());
        scheduler.scheduleJob(jobCheckout, checkoutTrigger);
        scheduler.scheduleJob(jobCheckDeadline, deadlineTrigger);
        scheduler.scheduleJob(jobPushNotifcation, pushNotiTrigger);

        logger.debug("Starting Scheduler threads");
        scheduler.getListenerManager().addJobListener(jobChainingJobListener);
        scheduler.start();
        return scheduler;
    }

    @Bean
    public boolean checkJob(Scheduler scheduler) throws IOException, SchedulerException {
        List<JobExecutionContext> jobs = scheduler.getCurrentlyExecutingJobs();
        for (JobExecutionContext job : jobs) {
            logger.debug("check job {}", job.getJobDetail().getDescription());
        }
        return true;
    }
}
