package com.datum.client.manager.mq.event;

import com.datum.client.manager.constants.CustomerStatus;

import java.util.UUID;

public record CustomerStatusChangeMessage(
        UUID eventId,
        String eventType,
        Long customerId,
        CustomerStatus status
) {
}