package com.pranit.accesshub.authentication.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AccountVerificationException extends BaseException {
    public AccountVerificationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
