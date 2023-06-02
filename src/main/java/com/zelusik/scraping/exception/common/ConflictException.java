package com.zelusik.scraping.exception.common;

import com.zelusik.scraping.exception.CustomException;
import org.springframework.http.HttpStatus;

public abstract class ConflictException extends CustomException {

    public ConflictException() {
        super(HttpStatus.CONFLICT);
    }

    public ConflictException(String optionalMessage) {
        super(HttpStatus.CONFLICT, optionalMessage);
    }

    public ConflictException(Throwable cause) {
        super(HttpStatus.CONFLICT, cause);
    }

    public ConflictException(String optionalMessage, Throwable cause) {
        super(HttpStatus.CONFLICT, optionalMessage, cause);
    }
}
