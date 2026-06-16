package com.kehai.api.batch;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.Instant;

public class NightlyScoringQuartzJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(NightlyScoringQuartzJob.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job nightlyScoringJob;


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("triggeredBy", "quartz")
                    .addLong("timestamp", Instant.now().toEpochMilli())
                    .toJobParameters();

            log.info("Quartz triggering nightlyScoringJob");
            jobLauncher.run(nightlyScoringJob, params);
        } catch (Exception ex) {
            log.error("Quartz failed to launch nigthlyScoringJob", ex);
        }
    }
}
