package com.client.manager.mq.event;

import java.time.Instant;
import java.util.UUID;

public record CustomerCreatedEvent(
        UUID eventId,
        String eventType,
        Long customerId,
        Instant createdAt
) {

    public CustomerCreatedEvent(Long customerId) {
        this(
                UUID.randomUUID(),
                "CUSTOMER_CREATED",
                customerId,
                Instant.now()
        );
    }
}
