package com.debitum.gateway.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(
        value = "Error",
        description = "Error respone"
)
public class ErrorDTO {

    @ApiModelProperty(value = "Error code", readOnly = true)
    private String errorCode;
    @ApiModelProperty(value = "Detailed error message", readOnly = true)
    private String errorMsg;
    @ApiModelProperty(value = "Stacktrace from back-end", readOnly = true)
    private String stacktrace;

    private ErrorDTO() {
    }

    public ErrorDTO(String errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorDTO(String errorCode,
                    String msg) {
        this.errorCode = errorCode;
        this.errorMsg = msg;
    }

    public ErrorDTO(String errorCode,
                    String errorMsg,
                    String stacktrace) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.stacktrace = stacktrace;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }
}