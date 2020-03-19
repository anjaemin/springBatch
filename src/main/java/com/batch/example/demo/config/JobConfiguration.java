package com.batch.example.demo.config;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
public class JobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    public JobConfiguration(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Bean
    public JobBuilderFactory jobBuilderFactory() {
        return new JobBuilderFactory(jobRepository);
    }

    @Bean
    public StepBuilderFactory stepBuilderFactory() {
        return new StepBuilderFactory(jobRepository, platformTransactionManager);
    }
}
