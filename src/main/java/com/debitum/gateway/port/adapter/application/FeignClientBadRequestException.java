package com.debitum.gateway.port.adapter.application;


import com.debitum.gateway.port.adapter.application.FeignClientResponseProcessor.FeignErrorMessage;

public class FeignClientBadRequestException extends Exception {
    private FeignErrorMessage error;

    public FeignClientBadRequestException(FeignErrorMessage error) {
        this.error = error;
    }

    public FeignErrorMessage getError() {
        return error;
    }

    public String getErrorCode() {
        return error.getErrorCode();
    }
}
