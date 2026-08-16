package com.pranit.accesshub.authentication.service.impl;

import com.pranit.accesshub.authentication.dto.EmailRequest;
import com.pranit.accesshub.authentication.dto.EmailResponse;
import com.pranit.accesshub.authentication.dto.OtpResponse;
import com.pranit.accesshub.authentication.dto.ResendOtpRequest;
import com.pranit.accesshub.authentication.exception.AccountVerificationException;
import com.pranit.accesshub.authentication.exception.EmailAlreadyExistsException;
import com.pranit.accesshub.authentication.exception.EmailNotFoundException;
import com.pranit.accesshub.authentication.exception.UserNotExistsException;
import com.pranit.accesshub.authentication.repository.UserRepository;
import com.pranit.accesshub.authentication.service.EmailService;
import com.pranit.accesshub.entities.entity.User;
import com.pranit.accesshub.helper.SecurityContext;
import com.pranit.accesshub.otp.constant.OtpPurpose;
import com.pranit.accesshub.otp.service.OtpService;
import com.pranit.accesshub.security.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final OtpService otpService;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public OtpResponse resendVerificationOtp(ResendOtpRequest request) {
        final String email = request.email().trim().toLowerCase();
        final OtpPurpose purpose = OtpPurpose.REGISTRATION;
        log.info("Processing OTP resend request for email: {}", email);
        final User user = findUserByEmail(email);
        validateAccountNotVerified(user, email);
        final String challengeId = sendOtp(user.getEmail(), purpose);
        log.info("Verification OTP resent successfully to email: {}", email);
        return OtpResponse.builder()
                .challengeId(challengeId)
                .message("Account activation otp resent successfully to registered email")
                .build();
    }

    private User findUserByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User email not found: {}", email);
                    return new EmailNotFoundException("User's email not found");
                });
    }

    private void validateAccountNotVerified(final User user, final String email) {
        if (!user.getEmail().equals(email)) {
            throw new EmailNotFoundException("Please provide registered email only");
        }
        if (user.isEnabled()) {
            log.warn("Account already verified for email: {}", user.getEmail());
            throw new AccountVerificationException("Account already verified");
        }
    }

    private String sendOtp(final String email, final OtpPurpose purpose) {
        return otpService.sendOtp(email, purpose);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EmailResponse requestCurrentEmailVerification(final EmailRequest request) {
        final UUID userId = SecurityContext.getCurrentUserId();
        final String email = request.email();
        final OtpPurpose purpose = OtpPurpose.EMAIL_VERIFICATION;
        verifyUserHasEmail(userId, email);
        final String challengeId = otpService.sendOtp(email, purpose);
        return EmailResponse.builder()
                .challengeId(challengeId)
                .message("Email verification OTP sent successfully to email")
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EmailResponse requestEmailChange(final EmailRequest request) {
        final String newEmail = request.newEmail();
        final OtpPurpose type = OtpPurpose.EMAIL_CHANGE;
        final UUID userId = SecurityContext.getCurrentUserId();
        final User user = findUserByUserId(userId);
        if (user.getEmail().equalsIgnoreCase(newEmail)) {
            log.warn("New email cannot be same as current email: {}", newEmail);
            throw new EmailAlreadyExistsException("New Email cannot be same as current email");
        }
        if (userRepository.existsByEmail(newEmail)) {
            log.warn("Email already in use: {}", newEmail);
            throw new EmailAlreadyExistsException("Email already in use");
        }
        final String challengeId = otpService.sendOtp(newEmail, type);
        log.info("Email change OTP sent successfully for userId: {}", userId);
        return EmailResponse.builder()
                .challengeId(challengeId)
                .message("Email change OTP sent successfully")
                .build();
    }

    private User findUserByUserId(final UUID userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new UserNotExistsException("User not found");
                });
    }

    private void verifyUserHasEmail(final UUID userId, final String email) {
        if (!userRepository.existsByUserIdAndEmail(userId, email)) {
            log.warn("Email id: {}, does not belong to current user with id: {}", email, userId);
            throw new UnauthorizedException("Email does not belong to current user");
        }
    }

}
