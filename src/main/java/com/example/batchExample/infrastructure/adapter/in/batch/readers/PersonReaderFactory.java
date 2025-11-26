package com.example.batchExample.infrastructure.adapter.in.batch.readers;

import com.example.batchExample.application.dto.PersonIn;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class PersonReaderFactory {
    @Bean
    @StepScope
    FlatFileItemReader<PersonIn> personCsvReader(@Value("#{jobParameters['inputFile']}") String inputFile) {
        return new FlatFileItemReaderBuilder<PersonIn>()
                .name("personInReader")
                .resource(new ClassPathResource(inputFile))
                .delimited()
                .names("firstName", "lastName")
                .fieldSetMapper(fs -> new PersonIn(fs.readString("firstName"), fs.readString("lastName")))
                .linesToSkip(1)
                .build();
    }
}
