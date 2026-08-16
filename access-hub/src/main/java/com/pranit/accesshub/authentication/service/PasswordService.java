package com.pranit.accesshub.authentication.service;

import com.pranit.accesshub.authentication.dto.ChangeForgotPasswordRequest;
import com.pranit.accesshub.authentication.dto.ForgotPasswordEmail;
import com.pranit.accesshub.authentication.dto.PasswordResponse;
import jakarta.validation.Valid;

public interface PasswordService {
    
    PasswordResponse requestPasswordReset(@Valid ForgotPasswordEmail request);

    PasswordResponse resetPassword(@Valid ChangeForgotPasswordRequest request);
}
