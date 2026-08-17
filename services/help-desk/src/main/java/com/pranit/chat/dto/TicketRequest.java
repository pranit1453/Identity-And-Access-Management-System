package com.pranit.chat.dto;

import com.pranit.chat.entities.constant.Category;
import com.pranit.chat.entities.constant.Priority;

import lombok.Builder;

@Builder
public record TicketRequest(
        String username,
        String email,
        String summary,
        Category category,
        Priority priority
) {
    public TicketRequest {
        if (category == null) category = Category.GENERAL;
        if (priority == null) priority = Priority.NEUTRAL;
    }
}
