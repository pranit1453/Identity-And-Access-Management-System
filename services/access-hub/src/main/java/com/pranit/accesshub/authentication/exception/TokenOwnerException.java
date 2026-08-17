package com.pranit.accesshub.authentication.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenOwnerException extends BaseException {
    public TokenOwnerException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
