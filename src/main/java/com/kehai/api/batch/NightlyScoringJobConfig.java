package com.kehai.api.batch;


import com.kehai.api.customer.Customer;
import com.kehai.api.scoring.ChurnRiskScore;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class NightlyScoringJobConfig {

    private static final Logger log = LoggerFactory.getLogger(NightlyScoringJobConfig.class);
    private static final int CHUNK_SIZE = 100;
    private static final int SKIP_LIMIT = 50;

    public static final String JOB_NAME = "nightlyScoringJob";

    @Bean
    public JpaPagingItemReader<Customer> customerReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Customer>()
                .name("activeCustomerReader")
                .entityManagerFactory(emf)
                .queryString("SELECT c FROM Customer c WHERE c.churned = false ORDER BY c.id")
                .pageSize(CHUNK_SIZE)
                .saveState(false)
                .build();
    }

    @Bean
    public ItemWriter<ChurnRiskScore> scoreItemWriter() {
        // Scores are already persisted by the process (inside scoringService.scoreCustomer).
        // The writer's only job is to mark the chunk as complete.
        return chunk -> log.info("Batch chunk complete: {} customers scored", chunk.size());
    }

    @Bean
    public Step nightlyScoringStep(JobRepository jobRepository,
                                   PlatformTransactionManager txManager,
                                   JpaPagingItemReader<Customer> customerReader,
                                   ScoringItemProcessor processor,
                                   ItemWriter<ChurnRiskScore> scoringWriter) {
        return new StepBuilder("nightlyScoringStep", jobRepository)
                .<Customer, ChurnRiskScore> chunk(CHUNK_SIZE, txManager)
                .reader(customerReader)
                .processor(processor)
                .writer(scoringWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(SKIP_LIMIT)
                .build();
    }

    @Bean
    public Job nightlyScoringJob(JobRepository jobRepository, Step nightlyScoringStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(nightlyScoringStep)
                .build();
    }
}
