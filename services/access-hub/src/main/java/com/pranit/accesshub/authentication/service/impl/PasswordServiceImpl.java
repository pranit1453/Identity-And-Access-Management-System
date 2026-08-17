package com.pranit.accesshub.authentication.service.impl;

import com.pranit.accesshub.authentication.dto.*;
import com.pranit.accesshub.authentication.exception.PasswordNotMatchingException;
import com.pranit.accesshub.authentication.exception.PasswordValidationException;
import com.pranit.accesshub.authentication.exception.UserNotExistsException;
import com.pranit.accesshub.authentication.repository.RefreshTokenRepository;
import com.pranit.accesshub.authentication.repository.UserRepository;
import com.pranit.accesshub.authentication.service.PasswordService;
import com.pranit.accesshub.entities.entity.User;
import com.pranit.accesshub.helper.SecurityContext;
import com.pranit.accesshub.otp.constant.OtpPurpose;
import com.pranit.accesshub.otp.service.OtpService;
import com.pranit.accesshub.redis.service.RedisTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final RedisTokenStore redisTokenStore;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PasswordResponse requestPasswordReset(final ForgotPasswordEmail request) {
        final String email = request.email();
        final OtpPurpose purpose = OtpPurpose.PASSWORD_CHANGE;
        final String challengeId = userRepository.findByEmail(email)
                .map(user -> {
                    final String id = otpService.sendOtp(user.getEmail(), purpose);
                    log.info("Forgot password OTP requested for userId: {}", user.getUserId());
                    return id;
                })
                .orElse(null);

        return PasswordResponse.builder()
                .challengeId(challengeId)
                .message("If the email is registered an OTP has been sent to your email.")
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PasswordResponse resetPassword(final ChangeForgotPasswordRequest request) {
        validatePasswordMatch(request.passwordRequest());
        final String email = request.email().trim().toLowerCase();
        final User user = findUserByEmail(email);
        if (passwordEncoder.matches(request.passwordRequest().password(), user.getPassword())) {
            log.warn("New password matches existing password for userId: {}", user.getUserId());
            throw new PasswordValidationException("New password cannot be same as old password");
        }
        user.setPassword(passwordEncoder.encode(request.passwordRequest().password()));
        invalidateUserSession(user.getUserId());
        log.info("Forgot password reset completed successfully for userId: {}", user.getUserId());
        return PasswordResponse.builder()
                .message("Password changed successfully")
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ChangePasswordResponse changeAccountPassword(final ChangePassword request) {
        final UUID userId = SecurityContext.getCurrentUserId();
        final User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for userId: {}", userId);
                    return new UserNotExistsException("User not found");
                });
        validateOldPassword(request.oldPassword(), user.getPassword());
        validatePasswordMatch(request.request());
        user.setPassword(passwordEncoder.encode(request.request().confirmPassword()));
        invalidateUserSession(user.getUserId());
        return ChangePasswordResponse.builder()
                .message("Password changed successfully. Please Login  again.")
                .build();
    }

    private void validateOldPassword(String requestPassword, String password) {
        if (!passwordEncoder.matches(requestPassword, password)) {
            throw new BadCredentialsException("Old password is incorrect");
        }
    }

    private void validatePasswordMatch(final PasswordRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            log.warn("Password and confirm password do not match");
            throw new PasswordNotMatchingException("Password and confirm password do not match");
        }
    }

    private User findUserByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User with email {} not found", email);
                    return new UserNotExistsException("User not found");
                });
    }

    private void invalidateUserSession(final UUID userId) {
        redisTokenStore.invalidateUserSession(userId);
        refreshTokenRepository.revokeAllByUserId(userId);
        log.info("User session invalidated for userId: {}", userId);
    }
}
