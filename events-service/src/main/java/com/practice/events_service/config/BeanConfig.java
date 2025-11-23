package com.practice.events_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.stats_client.StatsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public StatsClient statsClient() {
        return new StatsClient(objectMapper());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
