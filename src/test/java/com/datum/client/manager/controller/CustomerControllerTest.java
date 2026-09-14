package com.datum.client.manager.controller;

import com.datum.client.manager.constants.CustomerStatus;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static com.datum.client.manager.constants.Configuration.MAX_RESULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService service;

    @Mock
    private CreateCustomerRequest createRequest;

    @Mock
    private UpdateCustomerRequest updateRequest;

    @Mock
    private CustomerResponse customerResponse;

    @Mock
    private CustomerScoreResponse customerScoreResponse;

    @Mock
    private Pagenable<CustomerResponse> pagenableResponse;

    private CustomerController controller;

    @BeforeEach
    void setUp() {
        controller = new CustomerController(service);
    }

    @Test
    void shouldCreateCustomer() {
        when(service.create(createRequest)).thenReturn(10L);

        ResponseEntity<?> response = controller.create(createRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("client/10"), response.getHeaders().getLocation());
        assertNull(response.getBody());

        verify(service).create(createRequest);
    }

    @Test
    void shouldUpdateCustomer() {
        Long customerId = 10L;

        when(service.update(customerId, updateRequest))
                .thenReturn(customerResponse);

        ResponseEntity<CustomerResponse> response =
                controller.update(customerId, updateRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertSame(customerResponse, response.getBody());

        verify(service).update(customerId, updateRequest);
    }

    @Test
    void shouldDeleteCustomer() {
        Long customerId = 10L;

        controller.delete(customerId);

        verify(service).delete(customerId);
    }

    @Test
    void shouldRetrieveCustomerById() {
        Long customerId = 10L;

        when(service.findById(customerId)).thenReturn(customerResponse);

        ResponseEntity<CustomerResponse> response =
                controller.retrieve(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(customerResponse, response.getBody());

        verify(service).findById(customerId);
    }

    @Test
    void shouldFilterCustomersUsingAllParameters() {
        String name = "Leonardo";
        String email = "customer@email.com";
        String cpf = "12345678901";
        CustomerStatus status = CustomerStatus.ACTIVE;
        Long createdAtStart = 1_700_000_000_000L;
        Long createdAtEnd = 1_700_086_400_000L;
        Long updatedAtStart = 1_700_172_800_000L;
        Long updatedAtEnd = 1_700_259_200_000L;
        Integer page = 2;
        Integer max = 20;

        when(service.filter(org.mockito.ArgumentMatchers.any(CustomerFilter.class)))
                .thenReturn(pagenableResponse);

        ResponseEntity<Pagenable<CustomerResponse>> response =
                controller.filter(
                        name,
                        email,
                        cpf,
                        status,
                        createdAtStart,
                        createdAtEnd,
                        updatedAtStart,
                        updatedAtEnd,
                        page,
                        max
                );

        ArgumentCaptor<CustomerFilter> captor =
                ArgumentCaptor.forClass(CustomerFilter.class);

        verify(service).filter(captor.capture());

        CustomerFilter filter = captor.getValue();

        assertEquals(name, filter.name());
        assertEquals(email, filter.email());
        assertEquals(cpf, filter.cpf());
        assertEquals(status, filter.status());
        assertEquals(createdAtStart, filter.createdAtStart());
        assertEquals(createdAtEnd, filter.createdAtEnd());
        assertEquals(updatedAtStart, filter.updatedAtStart());
        assertEquals(updatedAtEnd, filter.updatedAtEnd());
        assertEquals(page, filter.page());
        assertEquals(max, filter.max());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(pagenableResponse, response.getBody());
    }

    @Test
    void shouldUseDefaultPageAndMaxWhenParametersAreNull() {
        when(service.filter(org.mockito.ArgumentMatchers.any(CustomerFilter.class)))
                .thenReturn(pagenableResponse);

        ResponseEntity<Pagenable<CustomerResponse>> response =
                controller.filter(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        ArgumentCaptor<CustomerFilter> captor =
                ArgumentCaptor.forClass(CustomerFilter.class);

        verify(service).filter(captor.capture());

        CustomerFilter filter = captor.getValue();

        assertNull(filter.name());
        assertNull(filter.email());
        assertNull(filter.cpf());
        assertNull(filter.status());
        assertNull(filter.createdAtStart());
        assertNull(filter.createdAtEnd());
        assertNull(filter.updatedAtStart());
        assertNull(filter.updatedAtEnd());
        assertEquals(0, filter.page());
        assertEquals(MAX_RESULT, filter.max());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(pagenableResponse, response.getBody());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101, Integer.MAX_VALUE})
    void shouldUseMaximumResultWhenMaxIsInvalid(int invalidMax) {
        when(service.filter(org.mockito.ArgumentMatchers.any(CustomerFilter.class)))
                .thenReturn(pagenableResponse);

        controller.filter(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                invalidMax
        );

        ArgumentCaptor<CustomerFilter> captor =
                ArgumentCaptor.forClass(CustomerFilter.class);

        verify(service).filter(captor.capture());

        assertEquals(MAX_RESULT, captor.getValue().max());
    }

    @Test
    void shouldPreserveMaxWhenItIsEqualToMaximumResult() {
        when(service.filter(org.mockito.ArgumentMatchers.any(CustomerFilter.class)))
                .thenReturn(pagenableResponse);

        controller.filter(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                MAX_RESULT
        );

        ArgumentCaptor<CustomerFilter> captor =
                ArgumentCaptor.forClass(CustomerFilter.class);

        verify(service).filter(captor.capture());

        assertEquals(MAX_RESULT, captor.getValue().max());
    }

    @Test
    void shouldPreserveZeroAsMax() {
        when(service.filter(org.mockito.ArgumentMatchers.any(CustomerFilter.class)))
                .thenReturn(pagenableResponse);

        controller.filter(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                0
        );

        ArgumentCaptor<CustomerFilter> captor =
                ArgumentCaptor.forClass(CustomerFilter.class);

        verify(service).filter(captor.capture());

        assertEquals(0, captor.getValue().max());
    }

    @Test
    void shouldReturnCustomerScore() {
        Long customerId = 10L;

        when(service.score(customerId)).thenReturn(customerScoreResponse);

        ResponseEntity<CustomerScoreResponse> response =
                controller.score(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(customerScoreResponse, response.getBody());

        verify(service).score(customerId);
    }
}