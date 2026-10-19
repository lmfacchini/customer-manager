package com.client.manager.controller;

import com.client.manager.dto.AddressResponse;
import com.client.manager.dto.PrioritizedAddressRequest;
import com.client.manager.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @Mock
    private AddressResponse addressResponse;

    @Mock
    private PrioritizedAddressRequest prioritizedAddressRequest;

    private AddressController controller;

    @BeforeEach
    void setUp() {
        controller = new AddressController(addressService);
    }

    @Test
    void shouldReturnAddressForCep() {
        when(addressService.findByCep("01001-000")).thenReturn(addressResponse);

        ResponseEntity<AddressResponse> response =
                controller.findByCep("01001-000");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(addressResponse, response.getBody());
        verify(addressService).findByCep("01001-000");
    }

    @Test
    void shouldSavePrioritizedAddress() {
        when(addressService.savePrioritized(prioritizedAddressRequest))
                .thenReturn(addressResponse);

        ResponseEntity<AddressResponse> response =
                controller.savePrioritized(prioritizedAddressRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(addressResponse, response.getBody());
        verify(addressService).savePrioritized(prioritizedAddressRequest);
    }

    @Test
    void shouldDeletePrioritizedAddress() {
        ResponseEntity<Void> response =
                controller.deletePrioritized("01001-000");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(addressService).deletePrioritized("01001-000");
    }
}
