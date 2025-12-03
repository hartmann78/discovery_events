package com.practice.stats_service.generators;

import com.practice.stats_dto.EndpointHit;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class EndpointHitGenerator {
    Random random = new Random();

    public EndpointHit generate(String app, String uri, LocalDateTime timestamp) {
        return EndpointHit.builder()
                .app(app)
                .uri(uri)
                .ip(generateIp())
                .timestamp(timestamp)
                .build();
    }

    private String generateIp() {
        return random.nextInt(256) + "." +
                random.nextInt(256) + "." +
                random.nextInt(256) + "." +
                random.nextInt(256);
    }
}
