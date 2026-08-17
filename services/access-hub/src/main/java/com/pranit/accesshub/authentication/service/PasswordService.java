package com.pranit.accesshub.authentication.service;

import com.pranit.accesshub.authentication.dto.*;
import jakarta.validation.Valid;

public interface PasswordService {

    PasswordResponse requestPasswordReset(@Valid ForgotPasswordEmail request);

    PasswordResponse resetPassword(@Valid ChangeForgotPasswordRequest request);

    ChangePasswordResponse changeAccountPassword(@Valid ChangePassword request);
}
