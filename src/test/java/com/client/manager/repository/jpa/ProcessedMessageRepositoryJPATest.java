package com.client.manager.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessedMessageRepositoryJPATest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private ProcessedMessageRepositoryJPA repository;

    @BeforeEach
    void setUp() {
        repository = new ProcessedMessageRepositoryJPA(entityManager);
    }

    @Test
    void shouldReturnTrueWhenMessageIsInserted() {
        UUID eventId = UUID.randomUUID();
        String eventType = "CUSTOMER_STATUS_CHANGE";

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("eventId", eventId)).thenReturn(query);
        when(query.setParameter("eventType", eventType)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        boolean result = repository.registerIfAbsent(eventId, eventType);

        assertTrue(result);

        verify(entityManager).createNativeQuery(anyString());
        verify(query).setParameter("eventId", eventId);
        verify(query).setParameter("eventType", eventType);
        verify(query).executeUpdate();
    }

    @Test
    void shouldReturnFalseWhenMessageAlreadyExists() {
        UUID eventId = UUID.randomUUID();
        String eventType = "CUSTOMER_STATUS_CHANGE";

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("eventId", eventId)).thenReturn(query);
        when(query.setParameter("eventType", eventType)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(0);

        boolean result = repository.registerIfAbsent(eventId, eventType);

        assertFalse(result);

        verify(entityManager).createNativeQuery(anyString());
        verify(query).setParameter("eventId", eventId);
        verify(query).setParameter("eventType", eventType);
        verify(query).executeUpdate();
    }

    @Test
    void shouldReturnFalseWhenAffectedRowsIsGreaterThanOne() {
        UUID eventId = UUID.randomUUID();
        String eventType = "CUSTOMER_STATUS_CHANGE";

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("eventId", eventId)).thenReturn(query);
        when(query.setParameter("eventType", eventType)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(2);

        boolean result = repository.registerIfAbsent(eventId, eventType);

        assertFalse(result);

        verify(entityManager).createNativeQuery(anyString());
        verify(query).setParameter("eventId", eventId);
        verify(query).setParameter("eventType", eventType);
        verify(query).executeUpdate();
    }
}