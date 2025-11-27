package com.example.batchExample.infrastructure.adapter.in.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TasksController {

    public static final String IMPORT_PERSONS_JOB = "import-persons-job";
    public static final String HELLO_TASKLET_JOB = "hello-tasklet-job";
    public static final String INPUT_CSV = "input.csv";
    public static final String CHUNK = "chunk";
    public static final String TASKLET = "tasklet";
    private final JobOperator jobOperator;

    public TasksController(JobOperator jobOperator){
        this.jobOperator = jobOperator;
    }

    @PostMapping("/chunk")
    public ResponseEntity<String> startChunk() throws Exception{

        Long jobExecution = jobOperator.start(IMPORT_PERSONS_JOB, props(CHUNK, INPUT_CSV));

        return ResponseEntity.ok("Chunk started " + jobExecution );
    }

    @PostMapping("/tasklet")
    public ResponseEntity<String> startTasklet() throws Exception{

        Long  jobExecution = jobOperator.start(HELLO_TASKLET_JOB, props(TASKLET, INPUT_CSV));

        return ResponseEntity.ok("tasklet started " + jobExecution );
    }

    private JobParameters params(String type, String file) {

        return new JobParametersBuilder()
                .addString("inputFile", file)  // identifying
                .addString("type", type)
                .addLong("ts", System.currentTimeMillis())
                .addString("execUUID", UUID.randomUUID().toString()) // <-- NUEVO
                .toJobParameters();
    }

    private Properties props(String type, String file){
        Properties props = new Properties();
        props.put("type", type);
        props.put("inputFile", file);
        //MultiResources reader
//        props.put("inputPattern", "classpath:/*.csv");
        props.put("outputFile", "/tmp/persons-out.csv");
        props.put("run.id", String.valueOf(System.currentTimeMillis())); // si quieres nueva instancia
        return props;
    }
}
