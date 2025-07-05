package com.fileprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FileProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileProcessorApplication.class, args);
    }

}
