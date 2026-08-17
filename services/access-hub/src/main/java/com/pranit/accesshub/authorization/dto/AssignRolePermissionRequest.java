package com.pranit.accesshub.authorization.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
public record AssignRolePermissionRequest(

        @NotNull(message = "Role ID is required")
        Long roleId,

        @NotNull(message = "Permission IDs are required")
        @Size(min = 1, message = "Please provide at least one permission ID")
        Set<Long> permissionIds
) {
}
