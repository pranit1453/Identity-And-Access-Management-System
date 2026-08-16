package com.pranit.accesshub.authentication.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AccountDeletedException extends BaseException {
    public AccountDeletedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
