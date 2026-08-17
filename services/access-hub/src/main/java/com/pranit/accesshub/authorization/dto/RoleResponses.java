package com.pranit.accesshub.authorization.dto;

import com.pranit.accesshub.entities.constant.Status;
import lombok.Builder;

@Builder
public record RoleResponses(
        Long roleId,
        String roleName,
        String roleDescription,
        Status status
) {
}
