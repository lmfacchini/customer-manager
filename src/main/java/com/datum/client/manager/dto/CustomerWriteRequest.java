package com.datum.client.manager.dto;

public sealed interface CustomerWriteRequest
        permits CreateCustomerRequest, UpdateCustomerRequest {

    String cpf();

    String email();
}
