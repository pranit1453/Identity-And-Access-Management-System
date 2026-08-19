package com.pranit.chat.security.exception;

import com.pranit.chat.exception.BaseException;
import org.springframework.http.HttpStatus;

public class KeyExtensionException extends BaseException {
    public KeyExtensionException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
