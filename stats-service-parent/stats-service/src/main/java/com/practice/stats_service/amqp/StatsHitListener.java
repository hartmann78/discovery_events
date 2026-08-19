package com.practice.stats_service.amqp;

import com.practice.stats_dto.EndpointHit;
import com.practice.stats_service.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Принимает EndpointHit из очереди RabbitMQ, которую наполняет events-service,
 * и сохраняет его тем же способом, что и REST-контроллер POST /hit.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatsHitListener {
    private final StatsService statsService;

    @RabbitListener(queues = "${events.service.queue}")
    public void handleHit(EndpointHit endpointHit) {
        log.info("Получено сообщение из очереди: {}", endpointHit);
        statsService.post(endpointHit);
    }
}
