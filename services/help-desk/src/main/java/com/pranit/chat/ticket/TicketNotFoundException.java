package com.pranit.chat.ticket;

import com.pranit.chat.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TicketNotFoundException extends BaseException {
    public TicketNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
