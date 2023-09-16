package com.litvintsev.accounts.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final ExceptionCode code;

    public NotFoundException(String message) {
        super(message);
        this.code = ExceptionCode.NOT_FOUND;
    }
}
