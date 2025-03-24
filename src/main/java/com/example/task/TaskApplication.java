package com.example.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication

	@EntityScan(basePackages = "com.example.task.Entity")
	public class TaskApplication {
	public static void main(String[] args) {
		SpringApplication.run(TaskApplication.class, args);
	}

}
