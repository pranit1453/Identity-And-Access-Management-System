package com.pranit.accesshub.controller;

import com.pranit.accesshub.authentication.dto.UpdateUserProfile;
import com.pranit.accesshub.authentication.dto.UserProfileResponse;
import com.pranit.accesshub.authentication.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
@Validated
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PutMapping(value = "/update", version = "v1")
    public ResponseEntity<String> updateProfile(@RequestBody @Valid UpdateUserProfile request) {
        userProfileService.updateUserProfile(request);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @GetMapping(value = "/me", version = "v1")
    public ResponseEntity<UserProfileResponse> fetchProfile() {
        return ResponseEntity.ok(userProfileService.fetchProfile());
    }

}
