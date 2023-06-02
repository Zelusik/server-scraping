package com.zelusik.scraping.dto.exception;

public record ValidationErrorDetails(
        Integer code,
        String field,
        String message
) {
}
