package com.datum.client.manager.service.impl;

import com.datum.client.manager.client.AddressApiClient;
import com.datum.client.manager.domain.Address;
import com.datum.client.manager.dto.AddressResponse;
import com.datum.client.manager.dto.PrioritizedAddressRequest;
import com.datum.client.manager.exception.BusinessException;
import com.datum.client.manager.exception.NotFoundException;
import com.datum.client.manager.mq.event.AddressLookupEvent;
import com.datum.client.manager.mq.publisher.AddressLookupPublisher;
import com.datum.client.manager.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository repository;

    @Mock
    private AddressApiClient addressApiClient;

    @Mock
    private AddressLookupPublisher eventPublisher;

    private AddressServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AddressServiceImpl(repository, addressApiClient, eventPublisher);
    }

    @Test
    void shouldReturnPriorityAddressAndPublishInternalLookupEvent() {
        Address address = new Address();
        address.setCep("01001000");
        address.setLogradouro("Praça da Sé");
        address.setBairro("Sé");
        address.setLocalidade("São Paulo");
        address.setUf("SP");
        when(repository.findByCep("01001000")).thenReturn(Optional.of(address));

        AddressResponse response = service.findByCep("01001-000");

        assertEquals("01001000", response.cep());
        assertEquals("Praça da Sé", response.logradouro());
        verify(addressApiClient, never()).findByCep(any());
        ArgumentCaptor<AddressLookupEvent> event =
                ArgumentCaptor.forClass(AddressLookupEvent.class);
        verify(eventPublisher).publish(event.capture());
        assertEquals("01001000", event.getValue().cep());
        assertEquals("INTERNAL", event.getValue().source());
        assertEquals(response, event.getValue().address());
    }

    @Test
    void shouldUseExternalApiWhenPriorityAddressDoesNotExist() {
        AddressResponse response = new AddressResponse(
                "01001000", "Praça da Sé", "", "", "Sé", "São Paulo",
                "SP", "São Paulo", "Sudeste", "3550308", "", "11", "7107", null
        );
        when(repository.findByCep("01001000")).thenReturn(Optional.empty());
        when(addressApiClient.findByCep("01001000")).thenReturn(response);

        assertEquals(response, service.findByCep("01001000"));

        verify(addressApiClient).findByCep("01001000");
        ArgumentCaptor<AddressLookupEvent> event =
                ArgumentCaptor.forClass(AddressLookupEvent.class);
        verify(eventPublisher).publish(event.capture());
        assertEquals("EXTERNAL", event.getValue().source());
        assertEquals(response, event.getValue().address());
    }

    @Test
    void shouldNormalizeAndPersistPrioritizedAddress() {
        PrioritizedAddressRequest request = new PrioritizedAddressRequest(
                "01001-000", " Praça da Sé ", null, null, "Sé", "São Paulo",
                "sp", null, null, null, null, null, null
        );
        when(repository.findByCep("01001000")).thenReturn(Optional.empty());
        when(repository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = service.savePrioritized(request);

        assertEquals("01001000", response.cep());
        assertEquals("Praça da Sé", response.logradouro());
        assertEquals("SP", response.uf());
    }

    @Test
    void shouldRejectMalformedCepBeforeAccessingRepository() {
        assertThrows(BusinessException.class, () -> service.findByCep("0100' OR 1=1"));

        verify(repository, never()).findByCep(any());
        verify(addressApiClient, never()).findByCep(any());
    }

    @Test
    void shouldPublishNotFoundExternalResponseBeforeReturningNotFound() {
        AddressResponse notFound = new AddressResponse(
                "99999999", null, null, null, null, null,
                null, null, null, null, null, null, null, true
        );
        when(repository.findByCep("99999999")).thenReturn(Optional.empty());
        when(addressApiClient.findByCep("99999999")).thenReturn(notFound);

        assertThrows(
                NotFoundException.class,
                () -> service.findByCep("99999999")
        );

        ArgumentCaptor<AddressLookupEvent> event =
                ArgumentCaptor.forClass(AddressLookupEvent.class);
        verify(eventPublisher).publish(event.capture());
        assertEquals("EXTERNAL", event.getValue().source());
        assertEquals(notFound, event.getValue().address());
    }

    @Test
    void shouldNormalizeCepBeforeDeletingPriorityAddress() {
        service.deletePrioritized(" 01001-000 ");

        verify(repository).deleteByCep("01001000");
    }
}
