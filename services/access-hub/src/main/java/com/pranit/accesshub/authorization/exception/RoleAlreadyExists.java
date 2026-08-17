package com.pranit.accesshub.authorization.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleAlreadyExists extends BaseException {
    public RoleAlreadyExists(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
