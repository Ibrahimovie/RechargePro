package job;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * @author kingfans
 */
public class Schedule {
    public static void run() throws SchedulerException {
        JobDetail jobDetail1 = JobBuilder.newJob(PortConnectionJob.class).withIdentity("connectionJob").build();
        SimpleTrigger trigger1 = TriggerBuilder.newTrigger().withIdentity("connectionTrigger").startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2).repeatForever()).build();
        JobDetail jobDetail2 = JobBuilder.newJob(StatusCheckJob.class).withIdentity("statusJob").build();
        SimpleTrigger trigger2 = TriggerBuilder.newTrigger().withIdentity("statusTrigger").startAt(new Date(System.currentTimeMillis() + 2000L)).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).repeatForever()).build();
        StdSchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        scheduler.start();
        scheduler.scheduleJob(jobDetail1, trigger1);
        scheduler.scheduleJob(jobDetail2, trigger2);
    }
}