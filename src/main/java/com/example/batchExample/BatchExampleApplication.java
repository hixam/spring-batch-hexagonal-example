package com.example.batchExample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
//@EnableRetry  //needed for spring retry
public class BatchExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchExampleApplication.class, args);
	}

}
