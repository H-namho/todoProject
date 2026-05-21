package com.example.memorypratice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MemoryPraticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoryPraticeApplication.class, args);
    }

}
