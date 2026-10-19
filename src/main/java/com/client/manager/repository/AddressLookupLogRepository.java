package com.client.manager.repository;

import com.client.manager.domain.AddressLookupLog;

public interface AddressLookupLogRepository {

    void saveIfAbsent(AddressLookupLog log);
}
