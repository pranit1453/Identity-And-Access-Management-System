package com.pranit.accesshub.authorization.dto;

import lombok.Builder;

@Builder
public record AssignResponse(
        boolean status,
        String message
) {
}
