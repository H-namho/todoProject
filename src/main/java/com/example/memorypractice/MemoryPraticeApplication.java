package com.example.memorypractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class MemoryPraticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoryPraticeApplication.class, args);
    }

}
