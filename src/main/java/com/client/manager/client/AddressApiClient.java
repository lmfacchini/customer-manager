package com.client.manager.client;

import com.client.manager.dto.AddressResponse;

public interface AddressApiClient {

    AddressResponse findByCep(String cep);
}
