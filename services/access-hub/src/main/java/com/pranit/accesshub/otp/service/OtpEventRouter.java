package com.pranit.accesshub.otp.service;

import com.pranit.accesshub.event.dto.OtpEvent;
import com.pranit.accesshub.otp.constant.OtpPurpose;

@FunctionalInterface
public interface OtpEventRouter {

    void publish(OtpPurpose purpose, OtpEvent event);
}
