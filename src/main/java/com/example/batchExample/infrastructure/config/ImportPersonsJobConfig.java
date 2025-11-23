package com.example.batchExample.infrastructure.config;

import com.example.batchExample.application.dto.PersonIn;
import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.application.port.in.NormalizePersonUseCase;
import com.example.batchExample.application.port.in.PersistPersonsUseCase;
import com.example.batchExample.infrastructure.adapter.in.batch.PersonItemProcessor;
import com.example.batchExample.infrastructure.adapter.out.batch.PersonItemWriter;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportPersonsJobConfig {

    @Bean
    ItemProcessor<PersonIn, PersonOut> personProcessor(NormalizePersonUseCase useCase) {
        return new PersonItemProcessor(useCase);
    }

    @Bean
    ItemWriter<PersonOut> personWriter(PersistPersonsUseCase useCase) {
        return new PersonItemWriter(useCase);
    }

    @Bean
    Step importPersonsStep(JobRepository jobRepository,
                           FlatFileItemReader<PersonIn> personCsvReader,
                           ItemProcessor<PersonIn, PersonOut> personProcessor,
                           ItemWriter<PersonOut> personWriter) {

        return new StepBuilder("import-persons-step", jobRepository)
                .<PersonIn, PersonOut>chunk(3)
                .reader(personCsvReader)
                .processor(personProcessor)
                .writer(personWriter)
                .build();
    }

    @Bean(name = "importPersonsJob")
    Job importPersonsJob(JobRepository jobRepository, Step importPersonsStep) {
        return new JobBuilder("import-persons-job", jobRepository)

                .start(importPersonsStep)
                .build();
    }
}
