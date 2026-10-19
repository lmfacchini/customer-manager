package com.datum.client.manager.repository;

import com.datum.client.manager.domain.Address;

import java.util.Optional;

public interface AddressRepository {

    Address save(Address address);

    Optional<Address> findByCep(String cep);

    void deleteByCep(String cep);
}
