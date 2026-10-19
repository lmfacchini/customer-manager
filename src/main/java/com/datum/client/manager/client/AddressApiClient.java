package com.datum.client.manager.client;

import com.datum.client.manager.dto.AddressResponse;

public interface AddressApiClient {

    AddressResponse findByCep(String cep);
}
