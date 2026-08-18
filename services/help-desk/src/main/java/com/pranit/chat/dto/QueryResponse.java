package com.pranit.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pranit.chat.entities.constant.Category;
import com.pranit.chat.entities.constant.Priority;
import com.pranit.chat.entities.constant.Status;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record QueryResponse(
        String message,
        List<AssistantResponse> response
) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AssistantResponse(
            String ticketId,
            String username,
            String summary,
            Category category,
            Priority priority,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }
}