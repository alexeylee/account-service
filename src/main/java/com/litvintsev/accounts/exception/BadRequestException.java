package com.litvintsev.accounts.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadRequestException extends RuntimeException {

    private final ExceptionCode code;

    public BadRequestException(String message) {
        super(message);
        this.code = ExceptionCode.BAD_REQUEST;
    }
}
