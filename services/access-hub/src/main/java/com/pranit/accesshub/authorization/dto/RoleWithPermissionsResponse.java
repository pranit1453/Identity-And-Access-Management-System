package com.pranit.accesshub.authorization.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RoleWithPermissionsResponse(
        Long roleId,
        String roleName,
        List<RolePermissionResponse> permissions
) {
}