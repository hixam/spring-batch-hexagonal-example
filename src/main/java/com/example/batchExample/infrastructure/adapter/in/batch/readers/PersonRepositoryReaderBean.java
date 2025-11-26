package com.example.batchExample.infrastructure.adapter.in.batch.readers;

import com.example.batchExample.application.dto.PersonIn;
import com.example.batchExample.infrastructure.adapter.out.persistence.entity.PersonRecord;
import com.example.batchExample.infrastructure.adapter.out.persistence.repository.PersonRecordRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PersonRepositoryReaderBean {

    @Bean
    @StepScope
    public RepositoryItemReader<PersonRecord> personRepositoryReader(PersonRecordRepository repo) {
        RepositoryItemReader<PersonRecord> reader = new RepositoryItemReader<>();
        reader.setName("personRepositoryReader");
        reader.setRepository(repo);
        reader.setMethodName("findByFirstName");
        reader.setArguments(List.of("HICHAM"));
        reader.setPageSize(100);
        reader.setSort(Map.of("id", Sort.Direction.ASC));
        // devuelve PersonEntity, así que mapea en processor:
        return reader;
    }

}
