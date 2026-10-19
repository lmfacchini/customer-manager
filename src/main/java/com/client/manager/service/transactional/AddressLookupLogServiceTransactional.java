package com.client.manager.service.transactional;

import com.client.manager.mq.event.AddressLookupEvent;
import com.client.manager.service.AddressLookupLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.TransactionOperations;

@RequiredArgsConstructor
public class AddressLookupLogServiceTransactional implements AddressLookupLogService {

    private final AddressLookupLogService delegate;
    private final TransactionOperations transactionOperations;

    @Override
    public void record(AddressLookupEvent event) {
        transactionOperations.executeWithoutResult(
                status -> delegate.record(event)
        );
    }
}
