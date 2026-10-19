package com.datum.client.manager.service.transactional;

import com.datum.client.manager.mq.event.AddressLookupEvent;
import com.datum.client.manager.service.AddressLookupLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionOperations;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressLookupLogServiceTransactionalTest {

    @Mock
    private AddressLookupLogService delegate;

    @Mock
    private TransactionOperations transactionOperations;

    @Mock
    private TransactionStatus transactionStatus;

    @Mock
    private AddressLookupEvent event;

    private AddressLookupLogServiceTransactional service;

    @BeforeEach
    void setUp() {
        service = new AddressLookupLogServiceTransactional(
                delegate,
                transactionOperations
        );
    }

    @Test
    void shouldRecordEventInsideTransaction() {
        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(transactionStatus);
            return null;
        }).when(transactionOperations).executeWithoutResult(any());

        service.record(event);

        verify(transactionOperations).executeWithoutResult(any());
        verify(delegate).record(event);
    }
}
