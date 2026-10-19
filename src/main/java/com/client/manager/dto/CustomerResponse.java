package com.client.manager.dto;

import com.client.manager.constants.CustomerStatus;

public record CustomerResponse(Long id, String name, String cpf, String email, CustomerStatus status) {
}
