package com.pranit.accesshub.authentication.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EmailNotFoundException extends BaseException {
    public EmailNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
