package com.pranit.accesshub.authorization.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record RevokeRolePermissionRequest(

        @NotNull(message = "Please provide role Id")
        Long roleId,

        @NotNull(message = "Please provide at least one permission Id")
        List<Long> permissionIds
) {
}
