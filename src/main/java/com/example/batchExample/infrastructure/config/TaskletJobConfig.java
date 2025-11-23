package com.example.batchExample.infrastructure.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

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

    @Bean(name = "helloTaskletJob")
    public Job helloTaskletJob(JobRepository jobRepository,
                               Step helloTaskletStep) {

        return new JobBuilder("hello-tasklet-job", jobRepository)

                .start(helloTaskletStep)
                .build();
    }
}
