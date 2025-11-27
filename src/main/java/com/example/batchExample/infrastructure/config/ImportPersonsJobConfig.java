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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportPersonsJobConfig {

    @Bean
    ItemProcessor<PersonIn, PersonOut> personProcessor(NormalizePersonUseCase useCase) {
        return new PersonItemProcessor(useCase);
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
    Step importPersonsStepComposite(JobRepository jobRepository,
                                    @Qualifier("personCsvReader") ItemReader<PersonIn> reader,
                                    ItemProcessor<PersonIn, PersonOut> personProcessor,
                                    @Qualifier("compositeWriter") CompositeItemWriter<PersonOut> compositeWriter,
                                    PlatformTransactionManager transactionManager) {

        return new StepBuilder("import-persons-composite-step", jobRepository)
                .<PersonIn, PersonOut>chunk(3, transactionManager)
                .reader(reader)
                .processor(personProcessor)
                .writer(compositeWriter)

                .faultTolerant()

                .retry(PessimisticLockingFailureException.class).retryLimit(3)

                .build();
    }

    @Bean
    Step importPersonsStep(JobRepository jobRepository,
                           FlatFileItemReader<PersonIn> personCsvReader,
                           ItemProcessor<PersonIn, PersonOut> personProcessor,
                           @Qualifier("personItemWriter") ItemWriter<PersonOut> personWriter,
                           PlatformTransactionManager transactionManager) {

        return new StepBuilder("import-persons-step", jobRepository)
                .<PersonIn, PersonOut>chunk(3, transactionManager)
                .reader(personCsvReader)
                .processor(personProcessor)
                .writer(personWriter)

                .faultTolerant()
                .skip(IllegalArgumentException.class).skipLimit(5)

                .build();
    }

    @Bean
    Step importPersonsStepMultiResources(JobRepository jobRepository,
                                         MultiResourceItemReader<PersonIn> multiCsvReader,
                                         ItemProcessor<PersonIn, PersonOut> personProcessor,
                                         @Qualifier("personItemWriter") ItemWriter<PersonOut> personWriter,
                                         PlatformTransactionManager transactionManager) {

        return new StepBuilder("import-persons-multi-step", jobRepository)
                .<PersonIn, PersonOut>chunk(3, transactionManager)
                .reader(multiCsvReader)
                .processor(personProcessor)
                .writer(personWriter)

                .faultTolerant()
                .retry(PessimisticLockingFailureException.class).retryLimit(3)
                .skip(IllegalArgumentException.class).skipLimit(5)

                .build();
    }

    @Bean
    Step importPersonsStepRepository(JobRepository jobRepository,
                                     RepositoryItemReader<PersonRecord> personRepositoryReader,
                                     ItemProcessor<PersonRecord, PersonOut> PersonEntityProcessor,
                                     @Qualifier("personItemWriter") ItemWriter<PersonOut> personWriter,
                                     PlatformTransactionManager transactionManager) {

        return new StepBuilder("import-persons-repository-step", jobRepository)
                .<PersonRecord, PersonOut>chunk(10, transactionManager)
                .reader(personRepositoryReader)
                .processor(PersonEntityProcessor)
                .writer(personWriter)
                .build();
    }


    @Bean(name = "importPersonsJob")
    Job importPersonsJob(JobRepository jobRepository, Step importPersonsStepComposite) {
        return new JobBuilder("import-persons-job", jobRepository)
//                .incrementer(new RunIdIncrementer())
                .start(importPersonsStepComposite)
                .build();
    }
}
