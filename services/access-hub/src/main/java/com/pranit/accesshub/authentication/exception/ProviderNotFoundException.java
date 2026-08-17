package com.pranit.accesshub.authentication.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProviderNotFoundException extends BaseException {
    public ProviderNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
