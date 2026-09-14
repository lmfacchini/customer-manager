package com.datum.client.manager.dto;

import com.datum.client.manager.constants.CustomerStatus;
import lombok.Builder;

@Builder
public record CustomerFilter(String name, String email, String cpf, CustomerStatus status, Long createdAtStart, Long createdAtEnd, Long updatedAtStart, Long updatedAtEnd, Integer page, Integer max) {
}
