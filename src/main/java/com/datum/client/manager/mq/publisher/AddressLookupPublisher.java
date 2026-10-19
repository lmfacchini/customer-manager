package com.datum.client.manager.mq.publisher;

import com.datum.client.manager.mq.event.AddressLookupEvent;

public interface AddressLookupPublisher {

    void publish(AddressLookupEvent event);
}
