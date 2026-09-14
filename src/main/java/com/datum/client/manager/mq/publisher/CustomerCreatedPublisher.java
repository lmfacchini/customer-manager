package com.datum.client.manager.mq.publisher;

import com.datum.client.manager.mq.event.CustomerCreatedEvent;

public interface CustomerCreatedPublisher {

    void publish(CustomerCreatedEvent event);
}
