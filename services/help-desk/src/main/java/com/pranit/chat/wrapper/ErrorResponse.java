package com.pranit.chat.wrapper;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(
        int status,
        String message,
        String path,
        Instant timestamp
) {
}