package com.batch.example.demo.job;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@StepScope
public class TestPartitioner implements Partitioner {

    //    @Value("#{jobExecutionContext['totalCount']}")
    private int totalCount = 1000;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitionMap = new HashMap<>();
        int pageSize = totalCount / gridSize;
        pageSize = pageSize % gridSize == 0 ? pageSize + 1 : pageSize;
        for (int i = 0; i < gridSize; i++) {
            if (totalCount < gridSize) {
                gridSize = totalCount;
            }
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.putInt("offset", i * pageSize);  //ExecutionContext에 페이지 정보 저장
            executionContext.putInt("limit", pageSize);
            partitionMap.put("test_partition_" + i, executionContext);
        }
        return partitionMap;

    }
}
