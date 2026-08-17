package com.pranit.accesshub.authorization.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicatePermissionName extends BaseException {
    public DuplicatePermissionName(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
