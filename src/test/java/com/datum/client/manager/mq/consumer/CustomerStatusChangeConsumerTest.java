package com.datum.client.manager.mq.consumer;

import com.datum.client.manager.constants.CustomerStatus;
import com.datum.client.manager.mq.event.CustomerStatusChangeMessage;
import com.datum.client.manager.repository.ProcessedMessageRepository;
import com.datum.client.manager.service.ProcessedMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerStatusChangeConsumerTest {

    private static final String SUPPORTED_EVENT_TYPE =
            "CUSTOMER_STATUS_CHANGE";

    @Mock
    private ProcessedMessageService processedMessageService;

    @Mock
    private ProcessedMessageRepository processedMessageRepository;

    @Mock
    private CustomerStatusChangeMessage message;

    private CustomerStatusChangeConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new CustomerStatusChangeConsumer(
                processedMessageService,
                processedMessageRepository
        );
    }

    @Test
    void shouldConsumeCustomerStatusChangeMessage() {
        UUID eventId = UUID.randomUUID();
        Long customerId = 10L;
        CustomerStatus status = CustomerStatus.INACTIVE;

        when(message.eventType()).thenReturn(SUPPORTED_EVENT_TYPE);
        when(message.eventId()).thenReturn(eventId);
        when(message.customerId()).thenReturn(customerId);
        when(message.status()).thenReturn(status);

        consumer.consume(message);

        verify(processedMessageService).changeStatus(
                eventId,
                customerId,
                status
        );
        verifyNoInteractions(processedMessageRepository);
    }

    @Test
    void shouldRejectUnsupportedEventType() {
        when(message.eventType()).thenReturn("UNSUPPORTED_EVENT");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> consumer.consume(message)
        );

        assertEquals(
                "Unsupported event type: UNSUPPORTED_EVENT",
                exception.getMessage()
        );

        verifyNoInteractions(processedMessageService);
        verifyNoInteractions(processedMessageRepository);
    }

    @Test
    void shouldRejectNullEventType() {
        when(message.eventType()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> consumer.consume(message)
        );

        assertEquals(
                "Unsupported event type: null",
                exception.getMessage()
        );

        verifyNoInteractions(processedMessageService);
        verifyNoInteractions(processedMessageRepository);
    }
}