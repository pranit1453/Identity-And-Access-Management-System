package com.pranit.accesshub.security.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class KeyNotLoadedException extends BaseException {
    public KeyNotLoadedException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
