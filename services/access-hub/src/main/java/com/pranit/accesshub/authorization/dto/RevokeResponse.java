package com.pranit.accesshub.authorization.dto;

import lombok.Builder;

@Builder
public record RevokeResponse(
        boolean status,
        String message
) {
}
