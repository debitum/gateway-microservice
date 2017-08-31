package com.debitum.gateway.port.adapter.security;

import com.debitum.gateway.domain.model.user.User;
import com.debitum.gateway.port.adapter.application.FeignClientResponseProcessor;
import com.debitum.gateway.port.adapter.application.FeignClientResponseProcessor.FeignErrorMessage;
import com.debitum.gateway.resource.ErrorCodedException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.debitum.gateway.port.adapter.application.FeignClientResponseProcessor.processFeignClientResponse;


@Component
class UserAuthServiceIntegration {

    private final Logger log = LoggerFactory.getLogger(UserAuthServiceIntegration.class);

    @Inject
    private AssetsRestClient userService;

    @HystrixCommand(
            groupKey = "UserAuthServiceIntegration",
            commandKey = "UserAuthServiceIntegration.findOneByLogin",
            ignoreExceptions = ErrorCodedException.class)
    public User findOneByLogin(String login) {
        return getResultOrThrow(processFeignClientResponse(() -> userService.findOneByLogin(login)));
    }

    private <T> T getResultOrThrow(FeignClientResponseProcessor.FeignClientResponse<T> response) {
        if (response.hasError()) {
            throw new ErrorCodedException(response.getError().map(FeignErrorMessage::getErrorCode).orElse(null));
        }
        return response.getResult().orElse(null);
    }

}
