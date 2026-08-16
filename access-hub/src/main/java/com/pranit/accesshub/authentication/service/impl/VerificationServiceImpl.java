package com.pranit.accesshub.authentication.service.impl;

import com.pranit.accesshub.authentication.dto.VerificationResponse;
import com.pranit.accesshub.authentication.dto.VerifyOtp;
import com.pranit.accesshub.authentication.exception.EmailNotFoundException;
import com.pranit.accesshub.authentication.exception.UserNotExistsException;
import com.pranit.accesshub.authentication.repository.UserRepository;
import com.pranit.accesshub.authentication.service.VerificationService;
import com.pranit.accesshub.entities.entity.User;
import com.pranit.accesshub.event.dto.SecurityEvent;
import com.pranit.accesshub.event.dto.WelcomeEvent;
import com.pranit.accesshub.event.service.EventPublisherService;
import com.pranit.accesshub.helper.Generate;
import com.pranit.accesshub.helper.SecurityContext;
import com.pranit.accesshub.otp.constant.OtpPurpose;
import com.pranit.accesshub.otp.service.OtpService;
import com.pranit.accesshub.security.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final EventPublisherService eventPublisherService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public VerificationResponse verifyRegistration(final VerifyOtp request) {
        final OtpPurpose purpose = OtpPurpose.REGISTRATION;
        final String email = verifyOtp(request.challengeId(), request.otp(), purpose);
        final User user = findUserByEmail(email);
        activateAccount(user);
        final WelcomeEvent data = WelcomeEvent.builder()
                .eventId(Generate.generateEventId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
        log.info("Sending Welcome email to username: {}", user.getUsername());
        eventPublisherService.sendWelcomeMessage(data);
        return VerificationResponse.builder()
                .message("Account verified successfully.")
                .build();
    }

    private String verifyOtp(String challengeId, String otp, OtpPurpose purpose) {
        return otpService.verifyOtp(challengeId, otp, purpose);
    }

    private User findUserByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User email not found: {}", email);
                    return new EmailNotFoundException("User's email not found");
                });
    }

    private void activateAccount(final User user) {
        if (!user.isEnabled()) {
            user.setEnabled(true);
            log.info("User account activated: {}", user.getEmail());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public VerificationResponse verifyPasswordResetOtp(final VerifyOtp request) {
        final OtpPurpose type = OtpPurpose.PASSWORD_CHANGE;
        final String email = verifyOtp(request.challengeId(), request.otp(), type);
        log.warn("Otp successfully verified for email: {}", email);
        return VerificationResponse.builder()
                .message("OTP verified successfully")
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public VerificationResponse confirmCurrentEmailVerification(final VerifyOtp request) {
        final OtpPurpose purpose = OtpPurpose.EMAIL_VERIFICATION;
        final UUID userId = SecurityContext.getCurrentUserId();
        final String email = verifyOtp(request.challengeId(), request.otp(), purpose);
        verifyUserHasEmail(userId, email);
        return VerificationResponse.builder()
                .message("Current email has been verified")
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public VerificationResponse confirmEmailChange(final VerifyOtp request) {
        final OtpPurpose type = OtpPurpose.EMAIL_CHANGE;
        final UUID userId = SecurityContext.getCurrentUserId();
        final User user = findUserByUserId(userId);
        final String oldEmail = user.getEmail();
        final String email = verifyOtp(request.challengeId(), request.otp(), type);
        user.setEmail(email);
        log.info("Email change OTP verified successfully for userId: {}", userId);
        final SecurityEvent data = SecurityEvent.builder()
                .eventId(Generate.generateEventId())
                .newMail(email)
                .oldMail(oldEmail)
                .timestamp(Instant.now())
                .build();
        eventPublisherService.sendSecurityAlertToOldEmail(data);
        return VerificationResponse.builder()
                .message("Email changed successfully")
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
