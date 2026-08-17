package com.pranit.accesshub.controller;

import com.pranit.accesshub.authentication.dto.*;
import com.pranit.accesshub.authentication.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Validated
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping(value = "/reset/request", version = "v1")
    public ResponseEntity<PasswordResponse> requestPasswordReset(@RequestBody @Valid ForgotPasswordEmail request) {
        final PasswordResponse response = passwordService.requestPasswordReset(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(value = "/reset", version = "v1")
    public ResponseEntity<PasswordResponse> resetPassword(@RequestBody @Valid ChangeForgotPasswordRequest request) {
        final PasswordResponse response = passwordService.resetPassword(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(value = "/change", version = "v1")
    public ResponseEntity<ChangePasswordResponse> changeAccountPassword(@RequestBody @Valid ChangePassword request) {
        final ChangePasswordResponse response = passwordService.changeAccountPassword(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
