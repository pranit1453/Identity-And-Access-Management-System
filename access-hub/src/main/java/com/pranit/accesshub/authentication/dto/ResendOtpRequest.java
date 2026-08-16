package com.pranit.accesshub.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ResendOtpRequest(
        @Email(message = "Invalid email foramt!!!")
        @NotBlank(message = "Email is required!!")
        String email
) {
}
