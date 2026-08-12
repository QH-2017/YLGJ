package com.ylgj.config;

import com.ylgj.job.OrderExpireJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz 定时任务配置
 * <p>
 * 注册"体检订单过期"定时任务，每天凌晨 1:00 执行
 */
@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail orderExpireJobDetail() {
        return JobBuilder.newJob(OrderExpireJob.class)
                .withIdentity("orderExpireJob")
                .storeDurably()
                .withDescription("体检订单过期自动更新定时任务")
                .build();
    }

    @Bean
    public Trigger orderExpireTrigger() {
        // Cron 表达式：每天 01:00:00 执行
        CronScheduleBuilder schedule = CronScheduleBuilder.cronSchedule("0 0 1 * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(orderExpireJobDetail())
                .withIdentity("orderExpireTrigger")
                .withSchedule(schedule)
                .build();
    }
}
