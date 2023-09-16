package com.litvintsev.accounts.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlreadyExistsException extends RuntimeException {

    private final ExceptionCode code;

    public AlreadyExistsException(String message) {
        super(message);
        this.code = ExceptionCode.ALREADY_EXISTS;
    }
}
