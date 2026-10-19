package com.datum.client.manager.service.transactional;

import com.datum.client.manager.dto.AddressResponse;
import com.datum.client.manager.dto.PrioritizedAddressRequest;
import com.datum.client.manager.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Objects;

@RequiredArgsConstructor
public class AddressServiceTransactional implements AddressService {

    private final AddressService delegate;
    private final TransactionOperations transactionOperations;

    @Override
    public AddressResponse findByCep(String cep) {
        return delegate.findByCep(cep);
    }

    @Override
    public AddressResponse savePrioritized(PrioritizedAddressRequest request) {
        return Objects.requireNonNull(
                transactionOperations.execute(status -> delegate.savePrioritized(request))
        );
    }

    @Override
    public void deletePrioritized(String cep) {
        transactionOperations.executeWithoutResult(
                status -> delegate.deletePrioritized(cep)
        );
    }
}
