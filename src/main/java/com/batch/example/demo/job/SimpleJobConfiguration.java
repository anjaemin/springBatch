package com.batch.example.demo.job;

import com.batch.example.demo.entity.TestEntity;
import com.batch.example.demo.repository.TestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;

@Slf4j
@Configuration
public class SimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TestRepository testRepository;
    private final TestPartitioner testPartitioner;
    private final TestItemReader testItemReader;

    public SimpleJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                                  TestRepository testRepository,
                                  TestPartitioner testPartitioner,
                                  TestItemReader testItemReader) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.testRepository = testRepository;
        this.testPartitioner = testPartitioner;
        this.testItemReader = testItemReader;
    }

    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1(null))
                .next(simpleStep2(null))
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is Step1");
                    log.info(">>>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
//                   throw new IllegalArgumentException("step1에서 실패합니다.");
                }).build();
    }

    @Bean
    @JobScope
    public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep2")
                .partitioner("partitionerStep", testPartitioner)
                .step(simpleStep3())
                .build();
    }

    @Bean
//    @JobScope
    public Step simpleStep3() {
        return stepBuilderFactory.get("simpleStep3")
                .chunk(100)
                .reader(testItemReader)
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Object> itemWriter() {
        return list -> list.forEach(m -> log.info("itemWriter"));

    }

    @Bean
    @StepScope
    public RepositoryItemReader<TestEntity> itemReader() {
        return new RepositoryItemReaderBuilder<TestEntity>()
                .name("repositoryItemReader")
                .repository(testRepository)
                .pageSize(100)
                .currentItemCount(0)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .methodName("findAll")
                .build();
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        int offset = stepExecution.getExecutionContext().getInt("offset");
        int limit = stepExecution.getExecutionContext().getInt("limit");
    }
}
