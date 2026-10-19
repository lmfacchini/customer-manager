package com.client.manager.service;

import com.client.manager.dto.AddressResponse;
import com.client.manager.dto.PrioritizedAddressRequest;

public interface AddressService {

    AddressResponse findByCep(String cep);

    AddressResponse savePrioritized(PrioritizedAddressRequest request);

    void deletePrioritized(String cep);
}
