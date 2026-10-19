package com.client.manager.dto;

import com.client.manager.constants.CustomerStatus;

public record CreateCustomerRequest(String name, String cpf, String email, CustomerStatus status) implements CustomerWriteRequest {
}
