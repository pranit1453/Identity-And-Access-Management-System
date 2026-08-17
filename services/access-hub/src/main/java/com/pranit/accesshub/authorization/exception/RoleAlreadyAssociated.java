package com.pranit.accesshub.authorization.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleAlreadyAssociated extends BaseException {
    public RoleAlreadyAssociated(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
