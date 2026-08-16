package com.pranit.accesshub.authentication.service;

import com.pranit.accesshub.authentication.dto.VerificationResponse;
import com.pranit.accesshub.authentication.dto.VerifyOtp;
import jakarta.validation.Valid;

public interface VerificationService {

    VerificationResponse verifyRegistration(@Valid VerifyOtp request);

    VerificationResponse verifyPasswordResetOtp(@Valid VerifyOtp request);

    VerificationResponse confirmCurrentEmailVerification(@Valid VerifyOtp request);

    VerificationResponse confirmEmailChange(@Valid VerifyOtp request);
}
