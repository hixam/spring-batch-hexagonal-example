package com.example.batchExample.infrastructure.adapter.out.batch;

import com.example.batchExample.application.dto.PersonOut;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class PersonFlatFileWriter {
    @Bean
    @StepScope
    public FlatFileItemWriter<PersonOut> personCsvWriter(
            @Value("#{jobParameters['outputFile']}") String outputFile) {

        return new FlatFileItemWriterBuilder<PersonOut>()
                .name("personCsvWriter")
                .resource(new FileSystemResource(outputFile))
                .delimited()
                .delimiter(",")
                .names("firstName", "lastName")
                .headerCallback(w -> w.write("Nombre,Apellido"))
                .build();
    }
}
