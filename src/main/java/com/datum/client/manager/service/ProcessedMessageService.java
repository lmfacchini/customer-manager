package com.datum.client.manager.service;

import com.datum.client.manager.constants.CustomerStatus;

import java.util.UUID;

public interface ProcessedMessageService {
    void changeStatus(UUID eventId, Long customerId, CustomerStatus status);
}
