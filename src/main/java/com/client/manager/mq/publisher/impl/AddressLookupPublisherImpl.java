package com.client.manager.mq.publisher.impl;

import com.client.manager.dto.CustomerRabbitProperties;
import com.client.manager.mq.event.AddressLookupEvent;
import com.client.manager.mq.publisher.AddressLookupPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressLookupPublisherImpl implements AddressLookupPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final CustomerRabbitProperties properties;

    @Override
    public void publish(AddressLookupEvent event) {
        rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.routingKeys().addressLookup(),
                event
        );
    }
}
