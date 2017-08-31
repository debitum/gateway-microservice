package com.debitum.gateway.resource;

import org.apache.commons.lang3.Validate;
import org.springframework.core.ErrorCoded;

public class ErrorCodedException extends RuntimeException implements ErrorCoded {

    private String code;

    public ErrorCodedException(String code) {
        Validate.notNull(code);
        this.code = code;
    }

    @Override
    public String getErrorCode() {
        return code;
    }
}
