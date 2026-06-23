package com.kehai.api.batch;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@Tag(
        name = "Administration",
        description = "Operational endpoints for triggering and inspecting batch jobs. Restricted to " +
                "users with the ROLE_ADMIN authority."
)
@RestController
@RequestMapping("/admin/jobs")
public class BatchAdminController {

    private static final Logger log = LoggerFactory.getLogger(BatchAdminController.class);

    private final JobLauncher jobLauncher;
    private final Job nightlyScoringJob;

    public BatchAdminController(JobLauncher jobLauncher, Job nightlyScoringJob) {
        this.jobLauncher = jobLauncher;
        this.nightlyScoringJob = nightlyScoringJob;
    }

    @Operation(
            summary = "Manually trigger the nightly scoring job",
            description = "Launches the same Spring Batch job that runs automatically at 02:00 UTC. " +
                    "Useful for re-running after an upstream data fix, or for testing in development. " +
                    "Returns immediately with the JobExecution ID; check application logs or the " +
                    "BATCH_JOB_EXECUTION table for completion status."
    )
    @PostMapping("/nightly-scoring/run")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> runNightlyScoring() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("triggeredBy", "manual")
                .addLong("timestamp", Instant.now().toEpochMilli())
                .toJobParameters();

        log.info("Manual trigger for nightlyScoringJob");
        JobExecution execution = jobLauncher.run(nightlyScoringJob, params);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "job_execution_id", execution.getId(),
                "status", execution.getStatus().toString(),
                "started_at", execution.getStartTime() != null
                    ? execution.getStartTime().toString() : "pending",
                "message", "Job launched. Check logs for progress."
        ));
    }
}
