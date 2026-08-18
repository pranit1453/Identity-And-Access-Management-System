package com.pranit.chat.security.exception;

import com.pranit.chat.exception.BaseException;
import org.springframework.http.HttpStatus;

public class KeyResourceNotFoundException extends BaseException {
    public KeyResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
