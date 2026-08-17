package com.pranit.accesshub.authorization.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserRoleNotFound extends BaseException {
    public UserRoleNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
