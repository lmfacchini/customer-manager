package com.client.manager.service.validation.impl;

import com.client.manager.domain.Customer;
import com.client.manager.dto.CreateCustomerRequest;
import com.client.manager.exception.BusinessException;
import com.client.manager.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerValidationServiceImplTest {

    private static final String CPF = "12345678901";
    private static final String EMAIL = "customer@email.com";

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CreateCustomerRequest request;

    @Mock
    private Customer existingCustomer;

    private CustomerValidationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CustomerValidationServiceImpl(customerRepository);
    }

    @Test
    void shouldValidateWhenCpfAndEmailDoNotExist() {
        when(request.cpf()).thenReturn(CPF);
        when(request.email()).thenReturn(EMAIL);
        when(customerRepository.findByCpf(CPF)).thenReturn(null);
        when(customerRepository.findByEmail(EMAIL)).thenReturn(null);

        assertDoesNotThrow(() -> service.validate(request));

        verify(customerRepository).findByCpf(CPF);
        verify(customerRepository).findByEmail(EMAIL);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenCpfAlreadyExists() {
        when(request.cpf()).thenReturn(CPF);
        when(customerRepository.findByCpf(CPF))
                .thenReturn(existingCustomer);

        assertThrows(
                BusinessException.class,
                () -> service.validate(request)
        );

        verify(customerRepository).findByCpf(CPF);
        verify(customerRepository, never()).findByEmail(anyString());
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
        when(request.cpf()).thenReturn(CPF);
        when(request.email()).thenReturn(EMAIL);
        when(customerRepository.findByCpf(CPF)).thenReturn(null);
        when(customerRepository.findByEmail(EMAIL))
                .thenReturn(existingCustomer);

        assertThrows(
                BusinessException.class,
                () -> service.validate(request)
        );

        verify(customerRepository).findByCpf(CPF);
        verify(customerRepository).findByEmail(EMAIL);
        verifyNoMoreInteractions(customerRepository);
    }
}