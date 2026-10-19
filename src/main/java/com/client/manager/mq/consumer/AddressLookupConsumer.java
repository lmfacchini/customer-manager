package com.client.manager.mq.consumer;

import com.client.manager.mq.event.AddressLookupEvent;
import com.client.manager.service.AddressLookupLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressLookupConsumer {

    private final AddressLookupLogService addressLookupLogService;

    @RabbitListener(queues = "${application.rabbitmq.queues.address-lookup}")
    public void consume(AddressLookupEvent event) {
        addressLookupLogService.record(event);
    }
}
