package com.example.batchExample.infrastructure.adapter.in.batch.readers;

import com.example.batchExample.application.dto.PersonIn;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MultiResourcesReaderBean {

    @Bean
    @StepScope
    public MultiResourceItemReader<PersonIn> multiCsvReader(
            @Value("#{jobParameters['inputPattern']}") String pattern) throws IOException {

        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources(pattern); // ej: "file:/tmp/in/*.csv"

        FlatFileItemReader<PersonIn> delegate =
                new FlatFileItemReaderBuilder<PersonIn>()
                        .name("delegateCsvReader")
                        .delimited()
                        .names("firstName", "lastName")
                        .fieldSetMapper(fs -> new PersonIn(
                                fs.readString("firstName"),
                                fs.readString("lastName")))
                        .linesToSkip(1)
                        .build();

        MultiResourceItemReader<PersonIn> reader = new MultiResourceItemReader<>();
        reader.setResources(resources);
        reader.setDelegate(delegate);
        return reader;
    }
}
