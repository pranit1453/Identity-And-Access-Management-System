package com.pranit.accesshub.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyEmail(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {
}
