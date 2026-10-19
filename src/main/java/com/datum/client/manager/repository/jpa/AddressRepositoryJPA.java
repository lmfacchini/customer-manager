package com.datum.client.manager.repository.jpa;

import com.datum.client.manager.domain.Address;
import com.datum.client.manager.repository.AddressRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepositoryJPA extends AddressRepository, JpaRepository<Address, String> {
}
