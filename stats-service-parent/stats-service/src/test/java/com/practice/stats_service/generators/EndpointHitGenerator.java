package com.practice.stats_service.generators;

import com.practice.stats_dto.EndpointHit;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class EndpointHitGenerator {
    Random random = new Random();

    public EndpointHit generate() {
        return EndpointHit.builder()
                .app("events-service")
                .uri("/events/1")
                .ip(generateIp())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    private String generateIp() {
        return random.nextInt(256) + "." +
                random.nextInt(256) + "." +
                random.nextInt(256) + "." +
                random.nextInt(256);
    }
}
