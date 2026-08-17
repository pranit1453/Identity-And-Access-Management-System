package com.pranit.accesshub.authentication.dto;

import lombok.Builder;

@Builder
public record TokenResponse(
        String message
) {
}
