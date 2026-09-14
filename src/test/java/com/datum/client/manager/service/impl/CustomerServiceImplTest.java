package com.datum.client.manager.service.impl;

import com.datum.client.manager.client.ScoresApiClient;
import com.datum.client.manager.constants.ClassificationType;
import com.datum.client.manager.domain.Customer;
import com.datum.client.manager.dto.CreateCustomerRequest;
import com.datum.client.manager.dto.CustomerFilter;
import com.datum.client.manager.dto.CustomerResponse;
import com.datum.client.manager.dto.CustomerScoreResponse;
import com.datum.client.manager.dto.Pagenable;
import com.datum.client.manager.dto.UpdateCustomerRequest;
import com.datum.client.manager.exception.NotFoundException;
import com.datum.client.manager.mapper.CustomerMapper;
import com.datum.client.manager.mq.event.CustomerCreatedEvent;
import com.datum.client.manager.mq.publisher.CustomerCreatedPublisher;
import com.datum.client.manager.repository.CustomerRepository;
import com.datum.client.manager.service.validation.CustomerValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerMapper mapper;

    @Mock
    private CustomerValidationService customerValidationService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerCreatedPublisher eventPublisher;

    @Mock
    private ScoresApiClient scoresApiClient;

    @Mock
    private Customer customer;

    @Mock
    private CreateCustomerRequest createRequest;

    @Mock
    private UpdateCustomerRequest updateRequest;

    @Mock
    private CustomerFilter customerFilter;

    @Mock
    private CustomerResponse customerResponse;

    private CustomerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CustomerServiceImpl(
                mapper,
                customerValidationService,
                customerRepository,
                eventPublisher,
                scoresApiClient
        );
    }

    @Test
    void shouldCreateCustomerAndPublishEvent() {
        when(mapper.toClient(createRequest)).thenReturn(customer);
        when(customer.getCpf()).thenReturn(" 12345678901 ");
        when(customer.getEmail()).thenReturn(" CUSTOMER@EMAIL.COM ");
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customer.getId()).thenReturn(10L);

        Long result = service.create(createRequest);

        assertEquals(10L, result);

        verify(customerValidationService).validate(createRequest);
        verify(mapper).toClient(createRequest);
        verify(customer).setCpf("12345678901");
        verify(customer).setEmail("customer@email.com");
        verify(customerRepository).save(customer);
        verify(eventPublisher).publish(any(CustomerCreatedEvent.class));
    }

    @Test
    void shouldNotPublishEventWhenRepositoryFailsDuringCreation() {
        when(mapper.toClient(createRequest)).thenReturn(customer);
        when(customer.getCpf()).thenReturn("12345678901");
        when(customer.getEmail()).thenReturn("customer@email.com");
        when(customerRepository.save(customer))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> service.create(createRequest));

        verify(customerValidationService).validate(createRequest);
        verify(customerRepository).save(customer);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldUpdateCustomer() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(updateRequest.name()).thenReturn("Customer Name");
        when(updateRequest.cpf()).thenReturn(" 12345678901 ");
        when(updateRequest.email()).thenReturn(" CUSTOMER@EMAIL.COM ");
        when(customerRepository.save(customer)).thenReturn(customer);
        when(mapper.toResponse(customer)).thenReturn(customerResponse);

        CustomerResponse result = service.update(10L, updateRequest);

        assertSame(customerResponse, result);

        verify(customerRepository).findById(10L);
        verify(customerValidationService).validate(updateRequest);
        verify(customer).setName("Customer Name");
        verify(customer).setCpf("12345678901");
        verify(customer).setStatus(updateRequest.status());
        verify(customer).setEmail("customer@email.com");
        verify(customerRepository).save(customer);
        verify(mapper).toResponse(customer);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonexistentCustomer() {
        when(customerRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.update(10L, updateRequest)
        );

        verify(customerRepository).findById(10L);
        verify(customerValidationService, never()).validate(updateRequest);
        verify(customerRepository, never()).save(any(Customer.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldFilterCustomersAndMapResponses() {
        Customer firstCustomer = org.mockito.Mockito.mock(Customer.class);
        Customer secondCustomer = org.mockito.Mockito.mock(Customer.class);
        CustomerResponse firstResponse =
                org.mockito.Mockito.mock(CustomerResponse.class);
        CustomerResponse secondResponse =
                org.mockito.Mockito.mock(CustomerResponse.class);

        Pagenable<Customer> repositoryResult =
                Pagenable.<Customer>builder()
                        .result(List.of(firstCustomer, secondCustomer))
                        .hasNext(true)
                        .build();

        when(customerRepository.filter(customerFilter))
                .thenReturn(repositoryResult);
        when(mapper.toResponse(firstCustomer)).thenReturn(firstResponse);
        when(mapper.toResponse(secondCustomer)).thenReturn(secondResponse);

        Pagenable<CustomerResponse> result = service.filter(customerFilter);

        assertEquals(List.of(firstResponse, secondResponse), result.result());
        assertTrue(result.hasNext());

        verify(customerRepository).filter(customerFilter);
        verify(mapper).toResponse(firstCustomer);
        verify(mapper).toResponse(secondCustomer);
    }

    @Test
    void shouldReturnEmptyResultWhenFilterFindsNoCustomers() {
        Pagenable<Customer> repositoryResult =
                Pagenable.<Customer>builder()
                        .result(List.of())
                        .hasNext(false)
                        .build();

        when(customerRepository.filter(customerFilter))
                .thenReturn(repositoryResult);

        Pagenable<CustomerResponse> result = service.filter(customerFilter);

        assertTrue(result.result().isEmpty());
        assertFalse(result.hasNext());

        verify(customerRepository).filter(customerFilter);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldFindCustomerById() {
        when(customerRepository.findById(10L))
                .thenReturn(Optional.of(customer));
        when(mapper.toResponse(customer)).thenReturn(customerResponse);

        CustomerResponse result = service.findById(10L);

        assertSame(customerResponse, result);

        verify(customerRepository).findById(10L);
        verify(mapper).toResponse(customer);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCustomerDoesNotExist() {
        when(customerRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.findById(10L)
        );

        verify(customerRepository).findById(10L);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldDeleteCustomerById() {
        doNothing().when(customerRepository).deleteById(10L);

        service.delete(10L);

        verify(customerRepository).deleteById(10L);
    }

    @ParameterizedTest
    @MethodSource("scoreClassifications")
    void shouldReturnCustomerScoreClassification(
            int score,
            ClassificationType expectedClassification
    ) {
        when(customerRepository.findById(10L))
                .thenReturn(Optional.of(customer));
        when(customer.getCpf()).thenReturn("12345678901");
        when(scoresApiClient.score("12345678901")).thenReturn(score);

        CustomerScoreResponse result = service.score(10L);

        assertEquals("12345678901", result.cpf());
        assertEquals(score, result.score());
        assertEquals(expectedClassification, result.classification());

        verify(customerRepository).findById(10L);
        verify(scoresApiClient).score("12345678901");
    }

    @Test
    void shouldThrowNotFoundExceptionWhenScoringNonexistentCustomer() {
        when(customerRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.score(10L)
        );

        verify(customerRepository).findById(10L);
        verifyNoInteractions(scoresApiClient);
    }

    private static Stream<Arguments> scoreClassifications() {
        return Stream.of(
                Arguments.of(0, ClassificationType.HIGH_RISK),
                Arguments.of(499, ClassificationType.HIGH_RISK),
                Arguments.of(500, ClassificationType.MEDIUM_RISK),
                Arguments.of(749, ClassificationType.MEDIUM_RISK),
                Arguments.of(750, ClassificationType.LOW_RISK),
                Arguments.of(950, ClassificationType.LOW_RISK)
        );
    }
}