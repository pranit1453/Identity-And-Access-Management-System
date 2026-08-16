package com.pranit.accesshub.authentication.dto;

import lombok.Builder;

@Builder
public record SignupResponse(
        String challengeId,
        String message
) {
}
