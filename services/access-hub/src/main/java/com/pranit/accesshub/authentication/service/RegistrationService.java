package com.pranit.accesshub.authentication.service;

import com.pranit.accesshub.authentication.dto.SignupRequest;
import com.pranit.accesshub.authentication.dto.SignupResponse;
import jakarta.validation.Valid;

public interface RegistrationService {
    
    SignupResponse createNewUserAccount(@Valid SignupRequest request);

    SignupResponse createNewAdminAccount(@Valid SignupRequest request);
}
