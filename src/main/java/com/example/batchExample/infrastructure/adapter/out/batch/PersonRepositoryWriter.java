package com.example.batchExample.infrastructure.adapter.out.batch;


import com.example.batchExample.infrastructure.adapter.out.persistence.entity.PersonRecord;
import com.example.batchExample.infrastructure.adapter.out.persistence.repository.PersonRecordRepository;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PersonRepositoryWriter {

    @Bean
    public RepositoryItemWriter<PersonRecord> repoPersonWriter(PersonRecordRepository repo) {
        RepositoryItemWriter<PersonRecord> writer = new RepositoryItemWriter<>();
        writer.setRepository(repo);
        writer.setMethodName("saveAll");
        return writer;
    }
}
