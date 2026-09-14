package com.datum.client.manager.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record Pagenable<T>(List<T> result, boolean hasNext) {
}
