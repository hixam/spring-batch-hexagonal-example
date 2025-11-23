package com.example.batchExample.infrastructure.adapter.in.batch;

import com.example.batchExample.application.dto.PersonIn;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class PersonReaderFactory {
    @Bean
    FlatFileItemReader<PersonIn> personCsvReader() {
        return new FlatFileItemReaderBuilder<PersonIn>()
                .name("personInReader")
                .resource(new ClassPathResource("input.csv"))
                .delimited()
                .names("firstName", "lastName")
                .fieldSetMapper(fs -> new PersonIn(fs.readString("firstName"), fs.readString("lastName")))
                .linesToSkip(1)
                .build();
    }
}
