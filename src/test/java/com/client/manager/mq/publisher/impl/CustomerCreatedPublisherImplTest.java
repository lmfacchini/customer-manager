package com.client.manager.mq.publisher.impl;

import com.client.manager.dto.CustomerRabbitProperties;
import com.client.manager.mq.event.CustomerCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerCreatedPublisherImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private CustomerRabbitProperties properties;

    @Mock
    private CustomerRabbitProperties.RoutingKeys routingKeys;

    @Mock
    private CustomerCreatedEvent event;

    private CustomerCreatedPublisherImpl publisher;

    @BeforeEach
    void setUp() {
        publisher = new CustomerCreatedPublisherImpl(
                rabbitTemplate,
                properties
        );
    }

    @Test
    void shouldPublishCustomerCreatedEvent() {
        String exchange = "customer.exchange";
        String routingKey = "customer.created";

        when(properties.exchange()).thenReturn(exchange);
        when(properties.routingKeys()).thenReturn(routingKeys);
        when(routingKeys.customerCreated()).thenReturn(routingKey);

        publisher.publish(event);

        verify(properties).exchange();
        verify(properties).routingKeys();
        verify(routingKeys).customerCreated();
        verify(rabbitTemplate).convertAndSend(
                exchange,
                routingKey,
                event
        );
    }
}