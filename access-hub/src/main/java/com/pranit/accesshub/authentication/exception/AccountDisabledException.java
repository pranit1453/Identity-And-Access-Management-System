package com.pranit.accesshub.authentication.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AccountDisabledException extends BaseException {
    public AccountDisabledException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
