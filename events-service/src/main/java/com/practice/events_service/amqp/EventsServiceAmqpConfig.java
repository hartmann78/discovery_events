package com.practice.events_service.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Объявляет ту же топологию (exchange/queue/binding), что и StatsServiceAmqpConfig
 * в stats-service. RabbitAdmin создаёт декларируемые здесь объекты при старте
 * контекста, поэтому не важно, какой из двух сервисов поднимется первым.
 */
@Configuration
public class EventsServiceAmqpConfig {

    @Bean
    public TopicExchange statsExchange(@Value("${events.service.exchange}") String exchange) {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public Queue statsQueue(@Value("${events.service.queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding statsBinding(Queue statsQueue, TopicExchange statsExchange,
                                 @Value("${events.service.routing-key}") String routingKey) {
        return BindingBuilder.bind(statsQueue).to(statsExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
