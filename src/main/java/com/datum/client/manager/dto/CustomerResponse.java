package com.datum.client.manager.dto;

import com.datum.client.manager.constants.CustomerStatus;

public record CustomerResponse(Long id, String name, String cpf, String email, CustomerStatus status) {
}
