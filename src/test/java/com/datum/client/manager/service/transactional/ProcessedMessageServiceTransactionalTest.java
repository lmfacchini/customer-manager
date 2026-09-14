package com.datum.client.manager.service.transactional;

import com.datum.client.manager.constants.CustomerStatus;
import com.datum.client.manager.service.ProcessedMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionOperations;

import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessedMessageServiceTransactionalTest {

    @Mock
    private ProcessedMessageService delegate;

    @Mock
    private TransactionOperations transactionOperations;

    @Mock
    private TransactionStatus transactionStatus;

    private ProcessedMessageServiceTransactional service;

    @BeforeEach
    void setUp() {
        service = new ProcessedMessageServiceTransactional(
                delegate,
                transactionOperations
        );
    }

    @Test
    void shouldChangeCustomerStatusInsideTransaction() {
        UUID eventId = UUID.randomUUID();
        Long customerId = 10L;
        CustomerStatus status = CustomerStatus.INACTIVE;

        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(transactionStatus);
            return null;
        }).when(transactionOperations).executeWithoutResult(any());

        service.changeStatus(eventId, customerId, status);

        verify(transactionOperations).executeWithoutResult(any());
        verify(delegate).changeStatus(eventId, customerId, status);
    }
}