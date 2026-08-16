package com.pranit.accesshub.authentication.dto;

import lombok.Builder;

@Builder
public record PasswordResponse(
        String challengeId,
        String message
) {
}
