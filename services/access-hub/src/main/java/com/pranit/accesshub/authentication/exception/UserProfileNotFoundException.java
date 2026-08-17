package com.pranit.accesshub.authentication.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserProfileNotFoundException extends BaseException {
    public UserProfileNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
