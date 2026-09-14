package com.datum.client.manager.service;

import com.datum.client.manager.dto.*;

public interface CustomerService {

    Long create(CreateCustomerRequest request);

    CustomerResponse update(Long id, UpdateCustomerRequest request);

    Pagenable<CustomerResponse> filter(CustomerFilter filter);

    CustomerResponse findById(Long id);

    void delete(Long id);

    CustomerScoreResponse score(Long id);

}
