package com.client.manager.service;

import com.client.manager.mq.event.AddressLookupEvent;

public interface AddressLookupLogService {

    void record(AddressLookupEvent event);
}
