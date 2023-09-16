package com.litvintsev.accounts.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleException(final RuntimeException exception,
                                             final HttpServletRequest request){

        log.error("Unexpected exception occurred: {}", ExceptionUtils.getRootCauseMessage(exception));
        return new ExceptionResponse(
                ExceptionCode.UNEXPECTED_EXCEPTION,
                ExceptionUtils.getRootCauseMessage(exception),
                request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBadRequestException(final MissingServletRequestParameterException exception,
                                                       final HttpServletRequest request){

        log.error("Request parameters not set. Exception: {}", ExceptionUtils.getRootCauseMessage(exception));
        return new ExceptionResponse(
                ExceptionCode.BAD_REQUEST,
                ExceptionUtils.getRootCauseMessage(exception),
                request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ExceptionResponse handleException(final NotFoundException exception,
                                             final HttpServletRequest request){

        log.error("{}", ExceptionUtils.getRootCauseMessage(exception));
        return new ExceptionResponse(
                exception.getCode(),
                exception.getMessage(),
                request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleException(final BadRequestException exception,
                                             final HttpServletRequest request){

        log.error("{}", ExceptionUtils.getRootCauseMessage(exception));
        return new ExceptionResponse(
                exception.getCode(),
                exception.getMessage(),
                request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({AlreadyExistsException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ExceptionResponse handleException(final AlreadyExistsException exception,
                                             final HttpServletRequest request){

        log.error("{}", ExceptionUtils.getRootCauseMessage(exception));
        return new ExceptionResponse(
                exception.getCode(),
                exception.getMessage(),
                request.getRequestURI());
    }
}
