package com.pranit.accesshub.controller;

import com.pranit.accesshub.authentication.dto.EmailRequest;
import com.pranit.accesshub.authentication.dto.EmailResponse;
import com.pranit.accesshub.authentication.dto.OtpResponse;
import com.pranit.accesshub.authentication.dto.ResendOtpRequest;
import com.pranit.accesshub.authentication.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Validated
public class EmailController {

    private final EmailService emailService;

    @PostMapping(value = "/resend", version = "v1")
    public ResponseEntity<OtpResponse> resendVerificationOtp(@RequestBody @Valid ResendOtpRequest request) {
        return ResponseEntity.ok(emailService.resendVerificationOtp(request));
    }

    @PostMapping(value = "/verification/request", version = "v1")
    public ResponseEntity<EmailResponse> requestCurrentEmailVerification(@RequestBody @Valid EmailRequest request) {
        final EmailResponse response = emailService.requestCurrentEmailVerification(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/change/request", version = "v1")
    public ResponseEntity<EmailResponse> requestEmailChange(@RequestBody @Valid EmailRequest request) {
        final EmailResponse response = emailService.requestEmailChange(request);
        return ResponseEntity.ok(response);
    }
    
}
