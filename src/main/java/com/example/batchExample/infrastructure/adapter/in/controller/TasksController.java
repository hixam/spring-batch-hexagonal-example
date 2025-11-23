package com.example.batchExample.infrastructure.adapter.in.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameter;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TasksController {

    private final JobOperator jobOperator;
    private final Job helloTaskletJob;
    private final Job importPersonsJob;

    public TasksController(JobOperator jobOperator, @Qualifier("helloTaskletJob") Job helloTaskletJob, @Qualifier("importPersonsJob") Job importPersonsJob){
        this.jobOperator = jobOperator;
        this.helloTaskletJob = helloTaskletJob;
        this.importPersonsJob = importPersonsJob;
    }

    @PostMapping("/chunk")
    public ResponseEntity<String> startChunk() throws JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        JobParameters params = params("chunk");
        System.out.println("CHUNK PARAMS => " + params);
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("id", UUID.randomUUID().toString());

        JobExecution jobExecution = jobOperator.start(importPersonsJob, builder.toJobParameters());

        return ResponseEntity.ok("Chunk started " + jobExecution.getJobInstanceId() );
    }

    @PostMapping("/tasklet")
    public ResponseEntity<String> startTasklet() throws JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        JobParameters params = params("tasklet");
        System.out.println("tasklet PARAMS => " + params);
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("id", UUID.randomUUID().toString());
        JobExecution jobExecution = jobOperator.start(helloTaskletJob, builder.toJobParameters());

        return ResponseEntity.ok("tasklet started " + jobExecution.getJobInstanceId() );
    }

    private JobParameters params(String type) {
        return new JobParametersBuilder()
                .addString("type", type)
                .addLong("ts", System.currentTimeMillis())
                .addString("execUUID", java.util.UUID.randomUUID().toString()) // <-- NUEVO
                .toJobParameters();
    }
}
