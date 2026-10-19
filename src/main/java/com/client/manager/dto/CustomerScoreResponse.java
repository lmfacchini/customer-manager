package com.client.manager.dto;

import com.client.manager.constants.ClassificationType;
import lombok.Builder;

@Builder
public record CustomerScoreResponse(String cpf,
                                    int score,
                                    ClassificationType classification) {
}
