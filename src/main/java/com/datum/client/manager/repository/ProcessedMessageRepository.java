package com.datum.client.manager.repository;

import java.util.UUID;

public interface ProcessedMessageRepository {

    boolean registerIfAbsent(
            UUID eventId,
            String eventType
    );
}
