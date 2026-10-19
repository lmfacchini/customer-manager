package com.client.manager.dto;

import com.client.manager.constants.CustomerStatus;
import lombok.Builder;

@Builder
public record CustomerFilter(String name, String email, String cpf, CustomerStatus status, Long createdAtStart, Long createdAtEnd, Long updatedAtStart, Long updatedAtEnd, Integer page, Integer max) {
}
