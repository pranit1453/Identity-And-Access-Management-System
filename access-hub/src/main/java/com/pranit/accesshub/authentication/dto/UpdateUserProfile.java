package com.pranit.accesshub.authentication.dto;

import com.pranit.accesshub.entities.constant.Gender;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserProfile(
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,
        Gender gender,
        @Past(message = "Date of birth must be in the past")
        LocalDate dob,
        String address

) {
}
