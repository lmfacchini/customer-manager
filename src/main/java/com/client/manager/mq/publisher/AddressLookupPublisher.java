package com.client.manager.mq.publisher;

import com.client.manager.mq.event.AddressLookupEvent;

public interface AddressLookupPublisher {

    void publish(AddressLookupEvent event);
}
