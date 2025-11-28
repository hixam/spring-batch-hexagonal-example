package com.example.batchExample.infrastructure.adapter.in.batch;

import com.example.batchExample.infrastructure.adapter.out.persistence.repository.PersonRecordRepository;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PersonIdRangePartitioner implements Partitioner {

    private final PersonRecordRepository repo;

    public PersonIdRangePartitioner(PersonRecordRepository repo) {
        this.repo = repo;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Long minId = repo.findMinId();
        Long maxId = repo.findMaxId();

        if (minId == null || maxId == null) {
            return Map.of(); // tabla vacía
        }

        Map<String, ExecutionContext> result = new HashMap<>();

        long targetSize = (maxId - minId + 1) / gridSize;
        long start = minId;
        long end = start + targetSize - 1;

        for (int i = 0; i < gridSize; i++) {
            if (end >= maxId) end = maxId;

            ExecutionContext ctx = new ExecutionContext();
            ctx.putLong("minId", start);
            ctx.putLong("maxId", end);

            result.put("partition" + i, ctx);

            start = end + 1;
            end = start + targetSize - 1;
        }

        return result;
    }
}