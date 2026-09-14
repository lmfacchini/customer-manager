package com.datum.client.manager.dto;

import com.datum.client.manager.constants.CustomerStatus;

public record UpdateCustomerRequest(String name, String cpf, String email, CustomerStatus status)implements CustomerWriteRequest {
}
