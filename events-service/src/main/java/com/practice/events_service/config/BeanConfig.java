package com.practice.events_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.practice.stats_client.StatsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public StatsClient statsClient(@Value("${spring.application.name}") String appName,
                                   @Value("${stats.service.host}") String host,
                                   @Value("${stats.service.port}") int port) {
        return new StatsClient(port, host, appName, objectMapper());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }
}
