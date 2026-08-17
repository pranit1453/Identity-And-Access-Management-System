package com.pranit.accesshub.authentication.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {
    public TokenExpiredException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
