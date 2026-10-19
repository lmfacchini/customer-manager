package com.client.manager.mq.event;

import com.client.manager.constants.CustomerStatus;

import java.util.UUID;

public record CustomerStatusChangeMessage(
        UUID eventId,
        String eventType,
        Long customerId,
        CustomerStatus status
) {
}