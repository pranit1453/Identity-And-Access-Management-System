package com.pranit.accesshub.authorization.dto;

import com.pranit.accesshub.entities.constant.Status;

import java.util.UUID;

public record UserRoleProjection(
        UUID userId,
        String username,
        Long roleId,
        String roleName,
        String roleDescription,
        Status status
) {
}
