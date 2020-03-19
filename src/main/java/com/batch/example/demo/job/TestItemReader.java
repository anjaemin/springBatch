package com.batch.example.demo.job;

import com.batch.example.demo.entity.TestEntity;
import com.batch.example.demo.repository.TestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@StepScope
public class TestItemReader extends RepositoryItemReader<TestEntity> {
    /*.name("repositoryItemReader")
                .repository(testRepository)
                .pageSize(100)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
            .methodName("findAll")*/
    private TestRepository testRepository;
    private int offset;
    private int limit;

    public TestItemReader(TestRepository testRepository) {
        super.setRepository(testRepository);
        super.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        super.setPageSize(100);
        super.setMethodName("findAll");
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        int offset = stepExecution.getExecutionContext().getInt("offset");
        int limit = stepExecution.getExecutionContext().getInt("limit");
        this.offset = offset;
        this.limit = limit;
    }


    @Override
    protected TestEntity doRead() throws Exception {
        log.info("TestItemReader doRead!!");
        int page = (offset - 1) / limit;
        int current = (offset - 1) % limit;
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.Direction.ASC, "id");

        Page<TestEntity> results = testRepository.findAll(pageRequest);
        return results.getContent().get(current++);
    }
}
