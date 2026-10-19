package com.client.manager.client.impl;

import com.client.manager.dto.AddressResponse;
import com.client.manager.exception.ClientAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.GET;

class ViaCepAddressApiClientTest {

    private MockRestServiceServer server;
    private ViaCepAddressApiClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://viacep.test");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ViaCepAddressApiClient(builder.build());
    }

    @Test
    void shouldRequestAndReturnAddressFromViaCep() {
        server.expect(requestTo("https://viacep.test/ws/01001000/json/"))
                .andExpect(method(GET))
                .andRespond(withSuccess("""
                        {
                          "cep": "01001-000",
                          "logradouro": "Praça da Sé",
                          "bairro": "Sé",
                          "localidade": "São Paulo",
                          "uf": "SP"
                        }
                        """, MediaType.APPLICATION_JSON));

        AddressResponse response = client.findByCep("01001000");

        assertEquals("01001-000", response.cep());
        assertEquals("Praça da Sé", response.logradouro());
        assertEquals("São Paulo", response.localidade());
        assertEquals("SP", response.uf());
        server.verify();
    }

    @Test
    void shouldReturnNotFoundFlagFromViaCepWithoutTreatingItAsTransportFailure() {
        server.expect(requestTo("https://viacep.test/ws/99999999/json/"))
                .andRespond(withSuccess("""
                        {"erro": true}
                        """, MediaType.APPLICATION_JSON));

        AddressResponse response = client.findByCep("99999999");

        assertEquals(Boolean.TRUE, response.erro());
        server.verify();
    }

    @Test
    void shouldWrapExternalServiceFailureWithoutExposingResponseDetails() {
        server.expect(requestTo("https://viacep.test/ws/01001000/json/"))
                .andRespond(withServerError()
                        .body("internal host and credentials")
                        .contentType(MediaType.TEXT_PLAIN));

        ClientAccessException exception = assertThrows(
                ClientAccessException.class,
                () -> client.findByCep("01001000")
        );

        assertEquals(502, exception.getStatus());
        assertEquals("ViaCEP request failed", exception.getMessage());
        server.verify();
    }

    @Test
    void shouldRejectEmptyExternalResponse() {
        server.expect(requestTo("https://viacep.test/ws/01001000/json/"))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(""));

        ClientAccessException exception = assertThrows(
                ClientAccessException.class,
                () -> client.findByCep("01001000")
        );

        assertEquals(502, exception.getStatus());
        assertEquals("ViaCEP returned an empty response", exception.getMessage());
        server.verify();
    }
}
