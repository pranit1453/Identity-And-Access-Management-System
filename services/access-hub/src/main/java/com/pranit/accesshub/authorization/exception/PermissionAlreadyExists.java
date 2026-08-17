package com.pranit.accesshub.authorization.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PermissionAlreadyExists extends BaseException {
    public PermissionAlreadyExists(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
