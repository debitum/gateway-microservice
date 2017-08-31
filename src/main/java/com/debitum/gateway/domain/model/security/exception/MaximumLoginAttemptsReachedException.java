package com.debitum.gateway.domain.model.security.exception;

import org.springframework.security.core.AuthenticationException;

public class MaximumLoginAttemptsReachedException extends AuthenticationException {

    public static final String MESSAGE = "Maximum login attempts reached";

    public MaximumLoginAttemptsReachedException() {
        super(MESSAGE);
    }
}
