package com.datum.client.manager.repository;

import com.datum.client.manager.domain.AddressLookupLog;

public interface AddressLookupLogRepository {

    void saveIfAbsent(AddressLookupLog log);
}
