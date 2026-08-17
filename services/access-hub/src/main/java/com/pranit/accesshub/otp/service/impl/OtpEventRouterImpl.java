package com.pranit.accesshub.otp.service.impl;

import com.pranit.accesshub.event.dto.OtpEvent;
import com.pranit.accesshub.event.service.EventPublisherService;
import com.pranit.accesshub.otp.constant.OtpPurpose;
import com.pranit.accesshub.otp.service.OtpEventRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpEventRouterImpl implements OtpEventRouter {

    private final EventPublisherService eventPublisherService;

    @Override
    public void publish(OtpPurpose purpose, OtpEvent event) {
        switch (purpose) {
            case REGISTRATION -> eventPublisherService.sendVerificationOtp(event);
            case FORGOT_PASSWORD -> eventPublisherService.sendPasswordChangeOtp(event);
            case EMAIL_CHANGE -> eventPublisherService.sendEmailChangeOtp(event);
            case EMAIL_VERIFICATION -> eventPublisherService.sendEmailVerifyData(event);
            default -> throw new IllegalArgumentException("Unsupported OTP purpose: " + purpose);
        }
    }
}
