package com.pranit.accesshub.otp.service;

import com.pranit.accesshub.otp.constant.OtpPurpose;

public interface OtpService {

    String sendOtp(String email, OtpPurpose purpose);

    String verifyOtp(String challengeId, String otp, OtpPurpose purpose);
}
