package com.datum.client.manager.service.impl;

import com.datum.client.manager.constants.CustomerStatus;
import com.datum.client.manager.repository.ProcessedMessageRepository;
import com.datum.client.manager.service.ProcessedMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ProcessedMessageServiceImpl implements ProcessedMessageService {

    private final ProcessedMessageRepository processedMessageRepository;

    @Override
    public void changeStatus(UUID eventId, Long customerId, CustomerStatus status) {
        boolean registered =
                processedMessageRepository.registerIfAbsent(
                        eventId,
                        "CUSTOMER_STATUS_CHANGE"
                );

        if (!registered) {
            log.warn("Unregistred event["+eventId+"] type[CUSTOMER_STATUS_CHANGE] status["+status+"] customer["+customerId+"]");
        }

    }
}
