package com.litvintsev.accounts.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExceptionResponse {

    private final LocalDateTime timestamp;
    private final ExceptionCode code;
    private final String error;
    private final String path;

    public ExceptionResponse(ExceptionCode code, String error, String path) {
        this.timestamp = LocalDateTime.now();
        this.code = code;
        this.error = error;
        this.path = path;
    }
}
