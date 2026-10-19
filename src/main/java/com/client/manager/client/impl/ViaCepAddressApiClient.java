package com.client.manager.client.impl;

import com.client.manager.client.AddressApiClient;
import com.client.manager.dto.AddressResponse;
import com.client.manager.exception.ClientAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@RequiredArgsConstructor
public class ViaCepAddressApiClient implements AddressApiClient {

    private final RestClient restClient;

    @Override
    public AddressResponse findByCep(String cep) {
        try {
            AddressResponse response = restClient.get()
                    .uri("/ws/{cep}/json/", cep)
                    .retrieve()
                    .body(AddressResponse.class);
            if (response == null) {
                throw new ClientAccessException("ViaCEP returned an empty response", 502);
            }
            return response;
        } catch (RestClientException exception) {
            throw new ClientAccessException("ViaCEP request failed", 502);
        }
    }
}
