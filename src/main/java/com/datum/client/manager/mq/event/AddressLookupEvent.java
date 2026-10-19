package com.datum.client.manager.mq.event;

import com.datum.client.manager.dto.AddressResponse;

import java.time.Instant;
import java.util.UUID;

public record AddressLookupEvent(
        UUID eventId,
        String eventType,
        Instant consultedAt,
        String cep,
        String source,
        AddressResponse address
) {

    public AddressLookupEvent(String cep, String source, AddressResponse address) {
        this(UUID.randomUUID(), "ADDRESS_LOOKUP", Instant.now(), cep, source, address);
    }
}
