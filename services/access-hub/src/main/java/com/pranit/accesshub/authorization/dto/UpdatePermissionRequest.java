package com.pranit.accesshub.authorization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdatePermissionRequest(

        @NotBlank(message = "Permission name is mandatory!!!")
        String permissionName,

        @Size(max = 255, message = "Permission description cannot exceed 255 characters.")
        String permissionDescription
) {
}
