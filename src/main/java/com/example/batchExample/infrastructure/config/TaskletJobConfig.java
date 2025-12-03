package com.example.batchExample.infrastructure.config;

import com.example.batchExample.application.dto.PersonIn;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.stream.IntStream;

import javax.sql.DataSource;

@Configuration
public class TaskletJobConfig {

    @Bean
    public Step helloTaskletStep(JobRepository jobRepository,
                                 PlatformTransactionManager txManager) {

        return new StepBuilder("hello-tasklet-step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("✅ Hola desde TaskletStep (web app viva)");
                    return RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }

    @Bean
    public StepExecutionListener importPersonsStepListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                System.out.println("STEP START -> " + stepExecution.getStepName());
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                System.out.println("""
                STEP END -> %s
                read=%d write=%d commits=%d rollbacks=%d skips(process=%d write=%d)
                """
                        .formatted(stepExecution.getStepName(),
                                stepExecution.getReadCount(),
                                stepExecution.getWriteCount(),
                                stepExecution.getCommitCount(),
                                stepExecution.getRollbackCount(),
                                stepExecution.getProcessSkipCount(),
                                stepExecution.getWriteSkipCount()
                        ));

                return stepExecution.getExitStatus();
            }
        };
    }

    @Bean
    public JobExecutionListener importPersonsJobListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                System.out.println("JOB START -> " + jobExecution.getJobInstance().getJobName()
                        + " params=" + jobExecution.getJobParameters());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                System.out.println("JOB END -> status=" + jobExecution.getStatus()
                        + " exit=" + jobExecution.getExitStatus());
            }
        };
    }

    @Bean
    public Step importPersonsJdbcStep(JobRepository jobRepository, PlatformTransactionManager txManager, DataSource dataSource) {
        return new StepBuilder("import-persons-jdbc-step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // Este tasklet solo lee y procesa los datos (sin escribir)
                    JdbcCursorItemReader<PersonIn> reader = new JdbcCursorItemReader<>();
                    reader.setDataSource(dataSource);
                    reader.setSql("SELECT first_name, last_name FROM person_out");
                    reader.setRowMapper((rs, rowNum) -> new PersonIn(rs.getString("first_name"), rs.getString("last_name")));

                    // Abrir el reader
                    reader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext());

                    // Imprimir los datos leídos
                    PersonIn person;
                    while ((person = reader.read()) != null) {
                        System.out.println("Person read: " + person.firstName() + " " + person.lastName());
                    }

                    // Cerrar el reader después de que se haya leído todo
                    reader.close();

                    return RepeatStatus.FINISHED; // Finaliza el Tasklet
                }, txManager)
                .listener(importPersonsJobListener())
                .listener(importPersonsStepListener())
                .build();
    }



    @Bean(name = "helloTaskletJob")
    public Job helloTaskletJob(JobRepository jobRepository,
                               Step helloTaskletStep, Step importPersonsJdbcStep) {

        return new JobBuilder("hello-tasklet-job", jobRepository)
//                .incrementer(new RunIdIncrementer())
                .start(helloTaskletStep)
                .next(importPersonsJdbcStep)
                .build();
    }

    public int applyDiscountToMaxPrice(int[] prices, int discount) {

        int max = IntStream.of(prices).max().orElse(0);
        int discountedPrice = (int)Math.round(max - (discount* 0.01));

        return discountedPrice;
    }
}
