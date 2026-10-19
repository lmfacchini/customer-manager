package com.datum.client.manager.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.rabbitmq")
public record CustomerRabbitProperties(
        String exchange,
        String deadLetterExchange,
        RoutingKeys routingKeys,
        Queues queues
) {

    public record RoutingKeys(
            String customerCreated,
            String customerStatusChange,
            String deadLetter,
            String addressLookup
    ) {
    }

    public record Queues(
            String customerCreated,
            String customerStatusChange,
            String deadLetter,
            String addressLookup
    ) {
    }
}
