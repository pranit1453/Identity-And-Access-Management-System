package com.pranit.accesshub.authorization.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PermissionNotFound extends BaseException {
    public PermissionNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
