package com.client.manager.service.impl;

import com.client.manager.constants.CustomerStatus;
import com.client.manager.repository.ProcessedMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessedMessageServiceImplTest {

    private static final String EVENT_TYPE = "CUSTOMER_STATUS_CHANGE";

    @Mock
    private ProcessedMessageRepository processedMessageRepository;

    private ProcessedMessageServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProcessedMessageServiceImpl(processedMessageRepository);
    }

    @Test
    void shouldRegisterMessageWhenEventHasNotBeenProcessed() {
        UUID eventId = UUID.randomUUID();
        Long customerId = 10L;
        CustomerStatus status = CustomerStatus.ACTIVE;

        when(processedMessageRepository.registerIfAbsent(eventId, EVENT_TYPE))
                .thenReturn(true);

        service.changeStatus(eventId, customerId, status);

        verify(processedMessageRepository)
                .registerIfAbsent(eventId, EVENT_TYPE);
        verifyNoMoreInteractions(processedMessageRepository);
    }

    @Test
    void shouldIgnoreMessageWhenEventHasAlreadyBeenProcessed() {
        UUID eventId = UUID.randomUUID();
        Long customerId = 10L;
        CustomerStatus status = CustomerStatus.INACTIVE;

        when(processedMessageRepository.registerIfAbsent(eventId, EVENT_TYPE))
                .thenReturn(false);

        service.changeStatus(eventId, customerId, status);

        verify(processedMessageRepository)
                .registerIfAbsent(eventId, EVENT_TYPE);
        verifyNoMoreInteractions(processedMessageRepository);
    }
}