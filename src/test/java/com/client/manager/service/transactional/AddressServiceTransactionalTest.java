package com.client.manager.service.transactional;

import com.client.manager.dto.AddressResponse;
import com.client.manager.dto.PrioritizedAddressRequest;
import com.client.manager.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTransactionalTest {

    @Mock
    private AddressService delegate;

    @Mock
    private TransactionOperations transactionOperations;

    @Mock
    private TransactionStatus transactionStatus;

    @Mock
    private PrioritizedAddressRequest request;

    @Mock
    private AddressResponse response;

    private AddressServiceTransactional service;

    @BeforeEach
    void setUp() {
        service = new AddressServiceTransactional(delegate, transactionOperations);
    }

    @Test
    void shouldLookUpCepWithoutOpeningTransaction() {
        when(delegate.findByCep("01001000")).thenReturn(response);

        assertSame(response, service.findByCep("01001000"));

        verify(delegate).findByCep("01001000");
        verifyNoInteractions(transactionOperations);
    }

    @Test
    void shouldSavePriorityAddressInsideTransaction() {
        when(transactionOperations.execute(any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(transactionStatus);
                });
        when(delegate.savePrioritized(request)).thenReturn(response);

        assertSame(response, service.savePrioritized(request));

        verify(transactionOperations).execute(any());
        verify(delegate).savePrioritized(request);
    }

    @Test
    void shouldRejectNullSaveResultFromTransaction() {
        when(transactionOperations.execute(any())).thenReturn(null);

        assertThrows(
                NullPointerException.class,
                () -> service.savePrioritized(request)
        );

        verify(delegate, never()).savePrioritized(request);
    }

    @Test
    void shouldDeletePriorityAddressInsideTransaction() {
        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(transactionStatus);
            return null;
        }).when(transactionOperations).executeWithoutResult(any());

        service.deletePrioritized("01001000");

        verify(transactionOperations).executeWithoutResult(any());
        verify(delegate).deletePrioritized("01001000");
    }
}
