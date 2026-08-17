package com.pranit.accesshub.authorization.exception;

import com.pranit.accesshub.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateRoleName extends BaseException {
    public DuplicateRoleName(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
