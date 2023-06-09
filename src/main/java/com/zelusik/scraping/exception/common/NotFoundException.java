package com.zelusik.scraping.exception.common;

import com.zelusik.scraping.exception.CustomException;
import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends CustomException {

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String optionalMessage) {
        super(HttpStatus.NOT_FOUND, optionalMessage);
    }

    public NotFoundException(Throwable cause) {
        super(HttpStatus.NOT_FOUND, cause);
    }

    public NotFoundException(String optionalMessage, Throwable cause) {
        super(HttpStatus.NOT_FOUND, optionalMessage, cause);
    }
}
