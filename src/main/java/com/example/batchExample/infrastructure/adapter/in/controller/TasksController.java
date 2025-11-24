package com.example.batchExample.infrastructure.adapter.in.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TasksController {

    private final JobOperator jobOperator;
//    private final Job helloTaskletJob;
//    private final Job importPersonsJob;

    public TasksController(JobOperator jobOperator){
        this.jobOperator = jobOperator;
//        this.helloTaskletJob = helloTaskletJob;
//        this.importPersonsJob = importPersonsJob;
    }

    @PostMapping("/chunk")
    public ResponseEntity<String> startChunk() throws Exception{
        JobParameters params = params("chunk");
        System.out.println("CHUNK PARAMS => " + params);

        Long jobExecution = jobOperator.startNextInstance("import-persons-job");

        return ResponseEntity.ok("Chunk started " + jobExecution );
    }

    @PostMapping("/tasklet")
    public ResponseEntity<String> startTasklet() throws Exception{
        JobParameters params = params("tasklet");
        System.out.println("tasklet PARAMS => " + params);
        Long  jobExecution = jobOperator.startNextInstance("hello-tasklet-job");

        return ResponseEntity.ok("tasklet started " + jobExecution );
    }

    private JobParameters params(String type) {
        return new JobParametersBuilder()
                .addString("type", type)
                .addLong("ts", System.currentTimeMillis())
                .addString("execUUID", UUID.randomUUID().toString()) // <-- NUEVO
                .toJobParameters();
    }
}
