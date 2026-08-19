package com.practice.events_service.amqp;

import com.practice.stats_dto.EndpointHit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Публикует факт обращения пользователя (EndpointHit) в очередь RabbitMQ
 * вместо синхронного HTTP-вызова POST /hit через StatsClient.
 */
@Slf4j
@Component
public class StatsEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final String appName;
    private final String exchange;
    private final String routingKey;

    public StatsEventPublisher(RabbitTemplate rabbitTemplate,
                                @Value("${spring.application.name}") String appName,
                                @Value("${events.service.exchange}") String exchange,
                                @Value("${events.service.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.appName = appName;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publishHit(HttpServletRequest request) {
        EndpointHit endpointHit = EndpointHit.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, endpointHit);
        } catch (Exception e) {
            log.warn("Не удалось отправить EndpointHit в RabbitMQ: {}", e.getMessage());
        }
    }
}
