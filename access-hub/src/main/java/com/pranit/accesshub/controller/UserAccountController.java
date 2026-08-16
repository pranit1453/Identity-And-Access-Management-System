package com.pranit.accesshub.controller;

import com.pranit.accesshub.authentication.service.AccountDeletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Validated
public class UserAccountController {

    private final AccountDeletionService accountDeletionService;

    @PostMapping(value = "/deactivate", version = "v1")
    public ResponseEntity<Void> deactivateUserAccount() {
        accountDeletionService.deleteUserAccount();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
}
