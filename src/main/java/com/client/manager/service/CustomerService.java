package com.client.manager.service;

import com.client.manager.dto.*;

public interface CustomerService {

    Long create(CreateCustomerRequest request);

    CustomerResponse update(Long id, UpdateCustomerRequest request);

    Pagenable<CustomerResponse> filter(CustomerFilter filter);

    CustomerResponse findById(Long id);

    void delete(Long id);

    CustomerScoreResponse score(Long id);

}
