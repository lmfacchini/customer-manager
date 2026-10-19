package com.datum.client.manager.service;

import com.datum.client.manager.mq.event.AddressLookupEvent;

public interface AddressLookupLogService {

    void record(AddressLookupEvent event);
}
