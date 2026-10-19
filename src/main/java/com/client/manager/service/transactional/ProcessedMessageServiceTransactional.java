package com.client.manager.service.transactional;

import com.client.manager.constants.CustomerStatus;
import com.client.manager.service.ProcessedMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.TransactionOperations;

import java.util.UUID;

@RequiredArgsConstructor
public class ProcessedMessageServiceTransactional implements ProcessedMessageService {

    private final ProcessedMessageService delegate;
    private final TransactionOperations transactionOperations;

    @Override
    public void changeStatus(UUID eventId, Long customerId, CustomerStatus status) {
        transactionOperations.executeWithoutResult(
                s -> delegate.changeStatus(eventId, customerId, status)
        );
    }
}
