package com.client.manager.mq.consumer;

import com.client.manager.mq.event.AddressLookupEvent;
import com.client.manager.service.AddressLookupLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressLookupConsumerTest {

    @Mock
    private AddressLookupLogService addressLookupLogService;

    @Mock
    private AddressLookupEvent event;

    private AddressLookupConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new AddressLookupConsumer(addressLookupLogService);
    }

    @Test
    void shouldDelegateConsumedEventToLogService() {
        consumer.consume(event);

        verify(addressLookupLogService).record(event);
    }
}
