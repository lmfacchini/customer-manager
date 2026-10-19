package com.client.manager.repository.jpa;

import com.client.manager.domain.Address;
import com.client.manager.repository.AddressRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepositoryJPA extends AddressRepository, JpaRepository<Address, String> {
}
