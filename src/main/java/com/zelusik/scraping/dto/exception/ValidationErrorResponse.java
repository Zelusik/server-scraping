package com.zelusik.scraping.dto.exception;

import java.util.List;

public record ValidationErrorResponse(
        Integer code,
        String message,
        List<ValidationErrorDetails> errors
) {
}
