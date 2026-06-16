package com.kehai.api.batch;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfig {

    @Value("${kehai.batch.nightly-scoring.cron}")
    private String nightlyScoringCron;

    @Bean
    public JobDetail nightlyScoringJobDetail() {
        return JobBuilder.newJob(NightlyScoringQuartzJob.class)
                .withIdentity("nightlyScoringQuartzJob")
                .withDescription("Triggers the nightly Spring Batch scoring job")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger nightlyScoringTrigger(JobDetail nightlyScoringJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(nightlyScoringJobDetail)
                .withIdentity("nightlyScoringTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(nightlyScoringCron))
                .build();
    }
}
