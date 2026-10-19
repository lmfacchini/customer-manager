package com.client.manager.mq.publisher;

import com.client.manager.mq.event.CustomerCreatedEvent;

public interface CustomerCreatedPublisher {

    void publish(CustomerCreatedEvent event);
}
