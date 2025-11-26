package com.example.batchExample.infrastructure.config;

import com.example.batchExample.application.dto.PersonIn;
import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.application.port.in.NormalizePersonUseCase;
import com.example.batchExample.application.port.in.PersistPersonsUseCase;
import com.example.batchExample.infrastructure.adapter.in.batch.processor.PersonItemProcessor;
import com.example.batchExample.infrastructure.adapter.out.batch.PersonItemWriter;
import com.example.batchExample.infrastructure.adapter.out.persistence.entity.PersonRecord;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportPersonsJobConfig {

    @Bean
    ItemProcessor<PersonIn, PersonOut> personProcessor(NormalizePersonUseCase useCase) {
        return new PersonItemProcessor(useCase);
    }

    @Bean
    @StepScope
    ItemWriter<PersonOut> personWriter(PersistPersonsUseCase useCase) {
        return new PersonItemWriter(useCase);
    }

//    @Bean //Need writer
//    Step importPersonsJdbcStep(JobRepository jobRepository,
//                               JdbcCursorItemReader<PersonIn> personJdbcReaderBean,
//                               PlatformTransactionManager transactionManager) {
//
//        return new StepBuilder("import-persons-jdbc-step", jobRepository)
//                .<PersonIn, PersonIn>chunk(3, transactionManager)
//                .reader(personJdbcReaderBean)  // El reader lee desde la base de datos
//                .build();
//    }

    @Bean
    Step importPersonsStep(JobRepository jobRepository,
                           FlatFileItemReader<PersonIn> personCsvReader,
                           ItemProcessor<PersonIn, PersonOut> personProcessor,
                           ItemWriter<PersonOut> personWriter,
                           PlatformTransactionManager transactionManager) {

        return new StepBuilder("import-persons-step", jobRepository)
                .<PersonIn, PersonOut>chunk(3, transactionManager)
                .reader(personCsvReader)
                .processor(personProcessor)
                .writer(personWriter)
                .build();
    }

    @Bean
    Step importPersonsStepMultiResources(JobRepository jobRepository,
                                         MultiResourceItemReader<PersonIn> multiCsvReader,
                                         ItemProcessor<PersonIn, PersonOut> personProcessor,
                                         ItemWriter<PersonOut> personWriter,
                                         PlatformTransactionManager transactionManager) {

        return new StepBuilder("import-persons-multi-step", jobRepository)
                .<PersonIn, PersonOut>chunk(3, transactionManager)
                .reader(multiCsvReader)
                .processor(personProcessor)
                .writer(personWriter)
                .build();
    }

    @Bean
    Step importPersonsStepRepository(JobRepository jobRepository,
                                     RepositoryItemReader<PersonRecord> personRepositoryReader,
                                     ItemProcessor<PersonRecord, PersonOut> PersonEntityProcessor,
                                     ItemWriter<PersonOut> personWriter,
                                     PlatformTransactionManager transactionManager) {

        return new StepBuilder("import-persons-repository-step", jobRepository)
                .<PersonRecord, PersonOut>chunk(10, transactionManager)
                .reader(personRepositoryReader)
                .processor(PersonEntityProcessor)
                .writer(personWriter)
                .build();
    }


    @Bean(name = "importPersonsJob")
    Job importPersonsJob(JobRepository jobRepository, Step importPersonsStepRepository) {
        return new JobBuilder("import-persons-job", jobRepository)
//                .incrementer(new RunIdIncrementer())
                .start(importPersonsStepRepository)
                .build();
    }
}
