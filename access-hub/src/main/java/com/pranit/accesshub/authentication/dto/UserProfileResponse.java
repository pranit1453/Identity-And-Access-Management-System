package com.pranit.accesshub.authentication.dto;


import com.pranit.accesshub.entities.constant.Gender;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record UserProfileResponse(
        UUID userId,
        String firstName,
        String lastName,
        String username,
        String email,
        Gender gender,
        LocalDate dob,
        String address
) {
}
