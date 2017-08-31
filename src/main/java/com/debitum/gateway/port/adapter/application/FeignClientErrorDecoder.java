package com.debitum.gateway.port.adapter.application;

import com.debitum.gateway.port.adapter.application.FeignClientResponseProcessor.FeignErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

@Component
class FeignClientErrorDecoder extends ErrorDecoder.Default {

    @Inject
    private ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 400) {
            FeignErrorMessage error;
            try {
                error = objectMapper.readValue(response.body().asInputStream(), FeignErrorMessage.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to map response to error message");
            }
            return new FeignClientBadRequestException(error);
        } else {
            return super.decode(methodKey, response);
        }
    }
}
