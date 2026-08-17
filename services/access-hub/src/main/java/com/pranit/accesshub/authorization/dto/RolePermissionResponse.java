package com.pranit.accesshub.authorization.dto;

import lombok.Builder;

@Builder
public record RolePermissionResponse(
        Long permissionId,
        String permissionName,
        String permissionDescription
) {
}
