package com.debitum.gateway.port.adapter.security.config;


import com.debitum.gateway.resource.ApiErrorCodes;
import com.debitum.gateway.resource.ErrorCodedException;
import com.debitum.gateway.resource.ErrorDTO;
import com.netflix.hystrix.exception.HystrixRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

@ControllerAdvice
class GlobalControllerExceptionHandler {

    Logger log = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDTO handle(AccessDeniedException e) {
        log.error(String.format("Invoked exception handler for %s", e.getClass()), e);
        return new ErrorDTO("ACCESS_DENIED", "ACCESS_DENIED");
    }

    @ExceptionHandler(InvalidGrantException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDTO handle(InvalidGrantException  e) {
        log.error(String.format("Invoked exception handler for %s", e.getClass()), e);
        return new ErrorDTO("ACCESS_DENIED_BAD_CREDENTIALS", "ACCESS_DENIED_BAD_CREDENTIALS");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDTO handle(UsernameNotFoundException e) {
        log.error(String.format("Invoked exception handler for %s", e.getClass()), e);
        return new ErrorDTO("ACCESS_DENIED_BAD_CREDENTIALS", "ACCESS_DENIED_BAD_CREDENTIALS");
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDTO handle(BadCredentialsException e) {
        log.error(String.format("Invoked exception handler for %s", e.getClass()), e);
        return new ErrorDTO("ACCESS_DENIED_BAD_CREDENTIALS", "ACCESS_DENIED_BAD_CREDENTIALS");
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handle(Exception e) {
        return handleDefault(e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handle(HttpMessageNotReadableException e) {
        log.error(String.format("Invoked exception handler for %s", e.getClass()), e);
        return new ErrorDTO(ApiErrorCodes.INVALID_REQUEST_CONTENT);
    }

    private ErrorDTO handleDefault(Exception e) {
        log.error(String.format("Invoked exception handler for %s", e.getClass()), e);
        return new ErrorDTO(ApiErrorCodes.UNEXPECTED, "Unexpected error occurred");
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handle(MissingServletRequestParameterException e) {
        log.error(String.format("Invoked exception handler for %s", e.getClass()), e);
        return new ErrorDTO(ApiErrorCodes.INVALID_REQUEST_PARAMS, e.getMessage());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handle(ConstraintViolationException e) {
        log.error(MessageFormat.format("Invoked exception handler for {0}. Constraints violated: {1}.", e.getClass(), e.getConstraintViolations().toString()), e);
        Map<String, Set<String>> result = e.getConstraintViolations()
                .stream()
                .collect(groupingBy(c -> {
                    String propertyName = null;
                    for (Path.Node node : c.getPropertyPath()) {
                        propertyName = node.getName();
                    }
                    return propertyName;
                }, mapping(ConstraintViolation::getMessage, toSet())));

        return new ErrorDTO(ApiErrorCodes.DATA_OBJECT_INVALID, result.toString());
    }

    @ExceptionHandler(HystrixRuntimeException.class)
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    @ResponseBody
    public ErrorDTO handle(HystrixRuntimeException e) {
        log.error(String.format("Invoked exception handler for %s", e.getClass()), e);
        return new ErrorDTO(ApiErrorCodes.EXTERNAL_SERVICE_TEMPORARILY_UNAVAILABLE, e.getFailureType().name());
    }

    @ExceptionHandler(ErrorCodedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handle(ErrorCodedException e) {
        log.error(String.format("Invoked exception handler for %s", e.getClass()), e);
        return new ErrorDTO(e.getErrorCode());
    }
}
