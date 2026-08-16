package com.pranit.accesshub.event.service;

import com.pranit.accesshub.event.dto.DeactivateAccountEvent;
import com.pranit.accesshub.event.dto.OtpEvent;
import com.pranit.accesshub.event.dto.SecurityEvent;
import com.pranit.accesshub.event.dto.WelcomeEvent;

public interface EventPublisherService {

    void sendVerificationOtp(OtpEvent data);

    void sendWelcomeMessage(WelcomeEvent data);

    void sendPasswordChangeOtp(OtpEvent data);

    void sendEmailChangeOtp(OtpEvent data);

    void sendSecurityAlertToOldEmail(SecurityEvent data);

    void sendEmailVerifyData(OtpEvent data);

    void sendSecurityAlertToUser(DeactivateAccountEvent event);
}
