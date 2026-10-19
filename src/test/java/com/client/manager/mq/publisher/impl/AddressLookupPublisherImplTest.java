package com.client.manager.mq.publisher.impl;

import com.client.manager.dto.CustomerRabbitProperties;
import com.client.manager.mq.event.AddressLookupEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressLookupPublisherImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private CustomerRabbitProperties properties;

    @Mock
    private CustomerRabbitProperties.RoutingKeys routingKeys;

    @Mock
    private AddressLookupEvent event;

    private AddressLookupPublisherImpl publisher;

    @BeforeEach
    void setUp() {
        publisher = new AddressLookupPublisherImpl(rabbitTemplate, properties);
    }

    @Test
    void shouldPublishLookupEventToConfiguredExchangeAndRoutingKey() {
        when(properties.exchange()).thenReturn("customer.events");
        when(properties.routingKeys()).thenReturn(routingKeys);
        when(routingKeys.addressLookup()).thenReturn("address.lookup");

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend(
                "customer.events",
                "address.lookup",
                event
        );
    }
}
