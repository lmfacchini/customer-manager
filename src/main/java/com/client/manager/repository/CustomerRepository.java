package com.client.manager.repository;

import com.client.manager.domain.Customer;
import com.client.manager.dto.CustomerFilter;
import com.client.manager.dto.Pagenable;

import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer client);

    Pagenable<Customer> filter(CustomerFilter filter);

    Optional<Customer> findById(Long id);

    void deleteById(Long id);

    Customer findByCpf(String document);

    Customer findByEmail(String email);
}
