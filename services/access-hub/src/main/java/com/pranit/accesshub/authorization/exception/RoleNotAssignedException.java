package com.pranit.accesshub.authorization.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleNotAssignedException extends BaseException {
    public RoleNotAssignedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
