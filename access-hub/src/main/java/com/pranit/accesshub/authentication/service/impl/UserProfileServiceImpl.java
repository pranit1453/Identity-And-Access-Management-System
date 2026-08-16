package com.pranit.accesshub.authentication.service.impl;

import com.pranit.accesshub.authentication.dto.UpdateUserProfile;
import com.pranit.accesshub.authentication.dto.UserProfileResponse;
import com.pranit.accesshub.authentication.exception.UserProfileNotFoundException;
import com.pranit.accesshub.authentication.repository.UserProfileRepository;
import com.pranit.accesshub.authentication.service.UserProfileService;
import com.pranit.accesshub.entities.entity.UserProfile;
import com.pranit.accesshub.helper.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public void updateUserProfile(final UpdateUserProfile request) {
        final UUID userId = SecurityContext.getCurrentUserId();
        final UserProfile userProfile = findProfileByUserId(userId);
        if (request.firstName() != null) userProfile.setFirstName(request.firstName());
        if (request.lastName() != null) userProfile.setLastName(request.lastName());
        if (request.address() != null) userProfile.setAddress(request.address());
        if (request.gender() != null) userProfile.setGender(request.gender());
        if (request.dob() != null) userProfile.setDob(request.dob());
    }

    private UserProfile findProfileByUserId(final UUID userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User profile not found for user ID: {}", userId);
                    return new UserProfileNotFoundException("User profile not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse fetchProfile() {
        final UUID userId = SecurityContext.getCurrentUserId();
        final UserProfile userProfile = findProfileByUserId(userId);
        return UserProfileResponse.builder()
                .userId(userProfile.getUserId())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .username(userProfile.getUser().getUsername())
                .email(userProfile.getUser().getEmail())
                .gender(userProfile.getGender())
                .dob(userProfile.getDob())
                .address(userProfile.getAddress())
                .build();
    }
}
