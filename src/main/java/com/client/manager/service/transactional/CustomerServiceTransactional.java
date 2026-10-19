package com.client.manager.service.transactional;

import com.client.manager.dto.*;
import com.client.manager.dto.*;
import com.client.manager.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Objects;

/**
 * Classe criada para não precisar colocar @Transacional e deixar o core da aplicação o mais desacoplado do framework possível
 */
@RequiredArgsConstructor
public class CustomerServiceTransactional implements CustomerService {

    private final CustomerService delegate;
    private final TransactionOperations transactionOperations;


    @Override
    public Long create(
            CreateCustomerRequest request
    ) {
        return Objects.requireNonNull(
                transactionOperations.execute(
                        status -> delegate.create(request)
                )
        );
    }

    @Override
    public CustomerResponse update(
            Long id,
            UpdateCustomerRequest request
    ) {
        return Objects.requireNonNull(
                transactionOperations.execute(
                        status -> delegate.update(id, request)
                )
        );
    }

    @Override
    public void delete(Long id) {
        transactionOperations.executeWithoutResult(
                status -> delegate.delete(id)
        );
    }

    @Override
    public CustomerScoreResponse score(Long id) {
        return delegate.score(id);
    }

    @Override
    public CustomerResponse findById(Long id) {
        return delegate.findById(id);
    }

    @Override
    public Pagenable<CustomerResponse> filter(
            CustomerFilter filter
    ) {
        return delegate.filter(filter);
    }


}
