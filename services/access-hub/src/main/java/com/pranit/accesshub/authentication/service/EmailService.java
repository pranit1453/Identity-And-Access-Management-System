package com.pranit.accesshub.authentication.service;

import com.pranit.accesshub.authentication.dto.EmailRequest;
import com.pranit.accesshub.authentication.dto.EmailResponse;
import com.pranit.accesshub.authentication.dto.OtpResponse;
import com.pranit.accesshub.authentication.dto.ResendOtpRequest;
import jakarta.validation.Valid;

public interface EmailService {
    OtpResponse resendVerificationOtp(@Valid ResendOtpRequest request);

    EmailResponse requestCurrentEmailVerification(@Valid EmailRequest request);

    EmailResponse requestEmailChange(@Valid EmailRequest request);
}
