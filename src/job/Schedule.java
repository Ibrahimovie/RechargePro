package job;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * @author kingfans
 */
public class Schedule {
    public static void run() throws SchedulerException {

        //串口连接监测
        JobDetail job1 = JobBuilder.newJob(PortConnectionJob.class).withIdentity("connectionJob").build();
        SimpleTrigger trigger1 = TriggerBuilder.newTrigger().withIdentity("connectionTrigger").startNow().
                withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2).repeatForever()).build();

        //服务器连接监测
        JobDetail job2 = JobBuilder.newJob(StatusCheckJob.class).withIdentity("statusJob").build();
        SimpleTrigger trigger2 = TriggerBuilder.newTrigger().withIdentity("statusTrigger").startAt(new Date(System.currentTimeMillis() + 2000L)).
                withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).repeatForever()).build();

//        //服务器连接监测
//        JobDetail job3 = JobBuilder.newJob(ServerConnectionJob.class).withIdentity("serverJob").build();
//        SimpleTrigger trigger3 = TriggerBuilder.newTrigger().withIdentity("serverTrigger").startNow().
//                withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2).repeatForever()).build();

        //定时推送发送失败的同步数据
//        JobDetail job4 = JobBuilder.newJob(PushMsgJob.class).withIdentity("pushMsgJob").build();
//        SimpleTrigger trigger4 = TriggerBuilder.newTrigger().withIdentity("pushMsgTrigger").startNow().
//                withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(10).repeatForever()).build();

        //定时获取串口信息
        JobDetail job5 = JobBuilder.newJob(AvailablePortJob.class).withIdentity("availablePortJob").build();
        SimpleTrigger trigger5 = TriggerBuilder.newTrigger().withIdentity("availablePortTrigger").startNow().
                withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(3).repeatForever()).build();

        StdSchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job1, trigger1);
        scheduler.scheduleJob(job2, trigger2);
//        scheduler.scheduleJob(job3, trigger3);
//        scheduler.scheduleJob(job4, trigger4);
        scheduler.scheduleJob(job5, trigger5);
    }
}
