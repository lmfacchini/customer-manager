package com.client.manager.dto;

import com.client.manager.constants.CustomerStatus;

public record UpdateCustomerRequest(String name, String cpf, String email, CustomerStatus status)implements CustomerWriteRequest {
}
