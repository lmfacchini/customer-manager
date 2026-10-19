package com.datum.client.manager.service;

import com.datum.client.manager.dto.AddressResponse;
import com.datum.client.manager.dto.PrioritizedAddressRequest;

public interface AddressService {

    AddressResponse findByCep(String cep);

    AddressResponse savePrioritized(PrioritizedAddressRequest request);

    void deletePrioritized(String cep);
}
