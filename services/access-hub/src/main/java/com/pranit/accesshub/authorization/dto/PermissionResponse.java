package com.pranit.accesshub.authorization.dto;

import lombok.Builder;

@Builder
public record PermissionResponse(
        Long permissionId,
        String permissionName,
        String permissionDescription
) {
}
