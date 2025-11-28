package com.example.batchExample.infrastructure.config;

import com.example.batchExample.application.dto.PersonIn;
import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.application.port.in.NormalizePersonUseCase;
import com.example.batchExample.application.port.in.PersistPersonsUseCase;
import com.example.batchExample.infrastructure.adapter.in.batch.PersonIdRangePartitioner;
import com.example.batchExample.infrastructure.adapter.in.batch.processor.PersonItemProcessor;
import com.example.batchExample.infrastructure.adapter.out.batch.PersonItemWriter;
import com.example.batchExample.infrastructure.adapter.out.persistence.entity.PersonRecord;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportPersonsJobConfig {

    @Bean
    ItemProcessor<PersonIn, PersonOut> personProcessor(NormalizePersonUseCase useCase) {
        return new PersonItemProcessor(useCase);
    }

    @Bean
    public ChunkListener chunkLoggingListener() {
        return new ChunkListener() {
            @Override
            public void beforeChunk(ChunkContext context) {
                System.out.println(">> CHUNK START");
            }

            @Override
            public void afterChunk(ChunkContext context) {
                System.out.println("<< CHUNK COMMIT OK");
            }

            @Override
            public void afterChunkError(ChunkContext context) {
                System.out.println("!! CHUNK ERROR -> rollback");
            }
        };
    }

    @Bean
    public ItemWriteListener<PersonOut> personWriteListener() {
        return new ItemWriteListener<>() {
            @Override
            public void beforeWrite(Chunk<? extends PersonOut> items) {
                // opcional
            }

            @Override
            public void afterWrite(Chunk<? extends PersonOut> items) {
                // opcional
            }

            @Override
            public void onWriteError(Exception ex, Chunk<? extends PersonOut> items) {
                System.out.println("WRITE ERROR -> items=" + items + " ex=" + ex);
            }
        };
    }

    @Bean
    public TaskExecutor jobTaskExecutor() {
        return new SimpleAsyncTaskExecutor("job-split-");
    }

    @Bean
    public Flow importPersonsFlow(Step importPersonsStep,
                                  Step importPersonsStepRepository) {

        return new FlowBuilder<Flow>("importPersonsFlow")
                .start(importPersonsStep)
                .next(importPersonsStepRepository)
                .end();
    }

    @Bean
    public Flow statsFlow(Step generateStatsStep) {
        return new FlowBuilder<Flow>("statsFlow")
                .start(generateStatsStep)
                .end();
    }

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
                .build();
    }

    @Bean
    Step generateStatsStep(JobRepository jobRepository,
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

                .listener(chunkLoggingListener())
                .listener(personWriteListener())

                .build();
    }

    @Bean(name = "importPersonsStep")
    Step importPersonsStep(JobRepository jobRepository,
                           @Qualifier("personCsvReader") FlatFileItemReader<PersonIn> personCsvReader,
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

    // SLAVE STEP PARTITION
    @Bean
    public Step personWorkerStep(JobRepository jobRepository,
                                 RepositoryItemReader<PersonRecord> personPartitionReader,
                                 ItemProcessor<PersonRecord, PersonOut> processor,
                                 @Qualifier("personItemWriter") ItemWriter<PersonOut> writer,
                                 PlatformTransactionManager txManager) {

        return new StepBuilder("person-worker-step", jobRepository)
                .<PersonRecord, PersonOut>chunk(100, txManager)
                .reader(personPartitionReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    //MASTER STEP PARTITION
    @Bean
    public Step personPartitionedStep(JobRepository jobRepository,
                                      PersonIdRangePartitioner partitioner,
                                      Step personWorkerStep,
                                      TaskExecutor jobTaskExecutor) {

        return new StepBuilder("person-partitioned-step", jobRepository)
                .partitioner("person-worker-step", partitioner)
                .step(personWorkerStep)
                .taskExecutor(jobTaskExecutor)
                .gridSize(4)
                .build();
    }

    @Bean
    public Job personPartitionJob(JobRepository jobRepository,
                                  Step personPartitionedStep) {

        return new JobBuilder("person-partition-job", jobRepository)
                .start(personPartitionedStep)
                .build();
    }

    @Bean(name = "importPersonsJob")
    Job importPersonsJob(JobRepository jobRepository,
                         @Qualifier("importPersonsStepComposite") Step importPersonsStepComposite) {
        return new JobBuilder("import-persons-job", jobRepository)
//                .incrementer(new RunIdIncrementer())
                .start(importPersonsStepComposite)
                .build();
    }

//    @Bean
//    public Job importPersonsJobFlow(JobRepository jobRepository,
//                                    @Qualifier("importPersonsStep") Step importPersonsStep,
//                                    Step publishOutboxStep) {
//
//        return new JobBuilder("import-persons-job-flow", jobRepository)
//                .start(importPersonsStep)
//                .on("COMPLETED").to(publishOutboxStep)
//                .from(importPersonsStep)
//                .on("COMPLETED WITH SKIPS").to(publishOutboxStep)
//                .from(importPersonsStep)
//                .on("FAILED").end()
//                .end()
//                .build();
//    }

    @Bean
    public Job importPersonsParallelJob(JobRepository jobRepository,
                                        Flow importPersonsFlow,
                                        Flow statsFlow,
                                        TaskExecutor jobTaskExecutor) {

        return new JobBuilder("import-persons-parallel-job", jobRepository)
                .start(importPersonsFlow)
                .split(jobTaskExecutor)
                .add(statsFlow)
                .end()
                .build();
    }
}
