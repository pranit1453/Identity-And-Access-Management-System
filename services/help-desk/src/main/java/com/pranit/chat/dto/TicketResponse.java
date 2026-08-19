package com.pranit.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pranit.chat.entities.constant.Category;
import com.pranit.chat.entities.constant.Priority;
import com.pranit.chat.entities.constant.Status;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TicketResponse(
        String ticketId,
        String username,
        String summary,
        Category category,
        Priority priority,
        Status status,
        Instant createdAt,
        Instant updatedAt,
        String message
) {
}
