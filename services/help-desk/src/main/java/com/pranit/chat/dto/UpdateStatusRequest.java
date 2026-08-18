package com.pranit.chat.dto;

import com.pranit.chat.entities.constant.Status;

public record UpdateStatusRequest(
        Status status
) {
}