package com.client.manager.mq.consumer;

import com.client.manager.mq.event.CustomerStatusChangeMessage;
import com.client.manager.repository.ProcessedMessageRepository;
import com.client.manager.service.ProcessedMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerStatusChangeConsumer {

    private final ProcessedMessageService processedMessageService;

    private final ProcessedMessageRepository processedMessageRepository;

    @RabbitListener(
            queues = "${application.rabbitmq.queues.customer-status-change}"
    )
    public void consume(CustomerStatusChangeMessage message) {
        if (!"CUSTOMER_STATUS_CHANGE".equals(message.eventType())) {
            throw new IllegalArgumentException(
                    "Unsupported event type: " + message.eventType()
            );
        }

        processedMessageService.changeStatus(
                message.eventId(),
                message.customerId(),
                message.status()
        );
    }
}
