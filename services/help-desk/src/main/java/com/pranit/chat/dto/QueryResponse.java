package com.pranit.chat.dto;

import lombok.Builder;

@Builder
public record QueryResponse(
        String message
) {
}
