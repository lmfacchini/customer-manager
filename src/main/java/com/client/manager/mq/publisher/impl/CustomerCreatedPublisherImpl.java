package com.client.manager.mq.publisher.impl;

import com.client.manager.dto.CustomerRabbitProperties;
import com.client.manager.mq.event.CustomerCreatedEvent;
import com.client.manager.mq.publisher.CustomerCreatedPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerCreatedPublisherImpl implements CustomerCreatedPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final CustomerRabbitProperties properties;

    public void publish(CustomerCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.routingKeys().customerCreated(),
                event
        );
    }
}
