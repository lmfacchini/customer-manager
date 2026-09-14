package com.datum.client.manager.service.transactional;

import com.datum.client.manager.dto.CreateCustomerRequest;
import com.datum.client.manager.dto.CustomerFilter;
import com.datum.client.manager.dto.CustomerResponse;
import com.datum.client.manager.dto.CustomerScoreResponse;
import com.datum.client.manager.dto.Pagenable;
import com.datum.client.manager.dto.UpdateCustomerRequest;
import com.datum.client.manager.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTransactionalTest {

    @Mock
    private CustomerService delegate;

    @Mock
    private TransactionOperations transactionOperations;

    @Mock
    private TransactionStatus transactionStatus;

    @Mock
    private CreateCustomerRequest createRequest;

    @Mock
    private UpdateCustomerRequest updateRequest;

    @Mock
    private CustomerFilter customerFilter;

    @Mock
    private CustomerResponse customerResponse;

    @Mock
    private CustomerScoreResponse customerScoreResponse;

    private CustomerServiceTransactional service;

    @BeforeEach
    void setUp() {
        service = new CustomerServiceTransactional(
                delegate,
                transactionOperations
        );
    }

    @Test
    void shouldCreateCustomerInsideTransaction() {
        Long expectedId = 10L;

        when(transactionOperations.execute(any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(transactionStatus);
                });

        when(delegate.create(createRequest)).thenReturn(expectedId);

        Long result = service.create(createRequest);

        assertEquals(expectedId, result);

        verify(transactionOperations).execute(any());
        verify(delegate).create(createRequest);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCreateTransactionReturnsNull() {
        when(transactionOperations.execute(any())).thenReturn(null);

        assertThrows(
                NullPointerException.class,
                () -> service.create(createRequest)
        );

        verify(transactionOperations).execute(any());
        verify(delegate, never()).create(createRequest);
    }

    @Test
    void shouldUpdateCustomerInsideTransaction() {
        Long customerId = 10L;

        when(transactionOperations.execute(any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(transactionStatus);
                });

        when(delegate.update(customerId, updateRequest))
                .thenReturn(customerResponse);

        CustomerResponse result = service.update(customerId, updateRequest);

        assertSame(customerResponse, result);

        verify(transactionOperations).execute(any());
        verify(delegate).update(customerId, updateRequest);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUpdateTransactionReturnsNull() {
        Long customerId = 10L;

        when(transactionOperations.execute(any())).thenReturn(null);

        assertThrows(
                NullPointerException.class,
                () -> service.update(customerId, updateRequest)
        );

        verify(transactionOperations).execute(any());
        verify(delegate, never()).update(customerId, updateRequest);
    }

    @Test
    void shouldDeleteCustomerInsideTransaction() {
        Long customerId = 10L;

        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(transactionStatus);
            return null;
        }).when(transactionOperations).executeWithoutResult(any());

        service.delete(customerId);

        verify(transactionOperations).executeWithoutResult(any());
        verify(delegate).delete(customerId);
    }

    @Test
    void shouldGetCustomerScoreWithoutTransaction() {
        Long customerId = 10L;

        when(delegate.score(customerId)).thenReturn(customerScoreResponse);

        CustomerScoreResponse result = service.score(customerId);

        assertSame(customerScoreResponse, result);

        verify(delegate).score(customerId);
        verifyNoInteractions(transactionOperations);
    }

    @Test
    void shouldFindCustomerByIdWithoutTransaction() {
        Long customerId = 10L;

        when(delegate.findById(customerId)).thenReturn(customerResponse);

        CustomerResponse result = service.findById(customerId);

        assertSame(customerResponse, result);

        verify(delegate).findById(customerId);
        verifyNoInteractions(transactionOperations);
    }

    @Test
    void shouldFilterCustomersWithoutTransaction() {
        Pagenable<CustomerResponse> expected =
                Pagenable.<CustomerResponse>builder()
                        .result(List.of(customerResponse))
                        .hasNext(false)
                        .build();

        when(delegate.filter(customerFilter)).thenReturn(expected);

        Pagenable<CustomerResponse> result = service.filter(customerFilter);

        assertSame(expected, result);

        verify(delegate).filter(customerFilter);
        verifyNoInteractions(transactionOperations);
    }
}