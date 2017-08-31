package com.debitum.gateway.port.adapter.application;


import com.netflix.hystrix.exception.HystrixRuntimeException;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class FeignClientResponseProcessor {

    public static <T> FeignClientResponse<T> processFeignClientResponse(Callable<T> callable) {
        Supplier<FeignClientResponse<T>> supplier = () -> {
            try {
                T call = callable.call();
                return new FeignClientResponse(call, null);
            } catch (HystrixRuntimeException ex) {
                Throwable exception = ex.getCause();
                if (exception instanceof FeignClientBadRequestException) {
                    FeignClientBadRequestException badRequestException = (FeignClientBadRequestException) exception;
                    return new FeignClientResponse(null, badRequestException.getError());
                } else {
                    throw ex;
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
        return supplier.get();
    }

    public static class FeignClientResponse<T> {
        private Optional<T> result;
        private Optional<FeignErrorMessage> error;

        public FeignClientResponse(T result, FeignErrorMessage error) {
            this.result = Optional.ofNullable(result);
            this.error = Optional.ofNullable(error);
        }

        public Optional<T> getResult() {
            return result;
        }

        public Optional<FeignErrorMessage> getError() {
            return error;
        }

        public boolean hasError() {
            return error.isPresent();
        }

    }

    public static class FeignErrorMessage {
        private String errorCode;

        FeignErrorMessage() {
        }

        public FeignErrorMessage(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }

}
