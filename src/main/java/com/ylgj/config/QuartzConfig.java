package com.ylgj.config;

import com.ylgj.job.OrderExpireJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz 定时任务配置
 * <p>
 * 注册"体检订单过期"定时任务，默认每天凌晨 1:00 执行，
 * Cron 表达式可通过配置项 quartz.order-expire-cron 覆盖（如测试环境可调快频率）
 */
@Configuration
public class QuartzConfig {

    @Value("${quartz.order-expire-cron:0 0 1 * * ?}")
    private String orderExpireCron;

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
        CronScheduleBuilder schedule = CronScheduleBuilder.cronSchedule(orderExpireCron);
        return TriggerBuilder.newTrigger()
                .forJob(orderExpireJobDetail())
                .withIdentity("orderExpireTrigger")
                .withSchedule(schedule)
                .build();
    }
}
