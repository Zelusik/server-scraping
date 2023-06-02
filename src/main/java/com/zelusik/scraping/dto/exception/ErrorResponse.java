package com.zelusik.scraping.dto.exception;

public record ErrorResponse(
        Integer code,
        String message
) {
}
