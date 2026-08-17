package com.pranit.accesshub.redis.model;

import com.pranit.accesshub.entities.constant.OtpStatus;
import com.pranit.accesshub.otp.constant.OtpPurpose;
import lombok.Builder;

@Builder
public record OtpVault(
        String email,
        String otp,
        OtpPurpose purpose,
        OtpStatus status
) {
}
