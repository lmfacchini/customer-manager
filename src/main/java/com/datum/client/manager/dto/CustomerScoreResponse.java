package com.datum.client.manager.dto;

import com.datum.client.manager.constants.ClassificationType;
import lombok.Builder;

@Builder
public record CustomerScoreResponse(String cpf,
                                    int score,
                                    ClassificationType classification) {
}
