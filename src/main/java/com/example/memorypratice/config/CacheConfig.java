package com.example.memorypratice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 중복코드 -> 추후 메서드로 분리
        cacheManager.registerCustomCache("userProfile",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofMinutes(15))
                        .build());
        cacheManager.registerCustomCache("todo",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .build());
        cacheManager.registerCustomCache("todoList",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .build());
        return cacheManager;
    }



}
