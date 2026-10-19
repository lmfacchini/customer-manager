package com.datum.client.manager.service.impl;

import com.datum.client.manager.domain.AddressLookupLog;
import com.datum.client.manager.dto.AddressResponse;
import com.datum.client.manager.mq.event.AddressLookupEvent;
import com.datum.client.manager.repository.AddressLookupLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressLookupLogServiceImplTest {

    @Mock
    private AddressLookupLogRepository repository;

    @Test
    void shouldPersistEventTimestampCepSourceAndReturnedAddress() {
        AddressLookupLogServiceImpl service = new AddressLookupLogServiceImpl(repository);
        UUID eventId = UUID.randomUUID();
        Instant consultedAt = Instant.parse("2026-10-19T12:00:00Z");
        AddressResponse response = new AddressResponse(
                "01001000", "Praça da Sé", "", "", "Sé", "São Paulo",
                "SP", "São Paulo", "Sudeste", "3550308", "", "11", "7107", null
        );
        AddressLookupEvent event = new AddressLookupEvent(
                eventId, "ADDRESS_LOOKUP", consultedAt, "01001000", "EXTERNAL", response
        );

        service.record(event);

        ArgumentCaptor<AddressLookupLog> log = ArgumentCaptor.forClass(AddressLookupLog.class);
        verify(repository).saveIfAbsent(log.capture());
        assertEquals(eventId, log.getValue().getEventId());
        assertEquals(consultedAt, log.getValue().getConsultedAt());
        assertEquals("01001000", log.getValue().getCep());
        assertEquals("EXTERNAL", log.getValue().getSource());
        assertEquals("Praça da Sé", log.getValue().getLogradouro());
        assertEquals("São Paulo", log.getValue().getLocalidade());
    }
}
