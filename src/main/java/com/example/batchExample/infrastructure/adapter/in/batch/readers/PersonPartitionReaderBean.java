package com.example.batchExample.infrastructure.adapter.in.batch.readers;

import com.example.batchExample.infrastructure.adapter.out.persistence.entity.PersonRecord;
import com.example.batchExample.infrastructure.adapter.out.persistence.repository.PersonRecordRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PersonPartitionReaderBean {

    @Bean(name = "personPartitionReader")
    @StepScope
    public RepositoryItemReader<PersonRecord> personPartitionReader(
            PersonRecordRepository repo,
            @Value("#{stepExecutionContext['minId']}") Long minId,
            @Value("#{stepExecutionContext['maxId']}") Long maxId
    ) {
        RepositoryItemReader<PersonRecord> reader = new RepositoryItemReader<>();
        reader.setName("personPartitionReader");
        reader.setRepository(repo);
        reader.setMethodName("findByIdBetween");
        reader.setArguments(List.of(minId, maxId));
        reader.setPageSize(100);
        reader.setSort(Map.of("id", Sort.Direction.ASC));
        return reader;
    }
}
