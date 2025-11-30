package com.practice.events_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.stats_client.StatsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public StatsClient statsClient(@Value("${stats.service.host}") String host, @Value("${stats.service.port}") int port) {
        return new StatsClient(objectMapper(), host, port);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
