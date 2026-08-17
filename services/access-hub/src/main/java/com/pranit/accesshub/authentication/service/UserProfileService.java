package com.pranit.accesshub.authentication.service;

import com.pranit.accesshub.authentication.dto.UpdateUserProfile;
import com.pranit.accesshub.authentication.dto.UserProfileResponse;
import jakarta.validation.Valid;

public interface UserProfileService {

    void updateUserProfile(@Valid UpdateUserProfile request);

    UserProfileResponse fetchProfile();
}
