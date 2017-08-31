package com.debitum.gateway.port.adapter.security;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
class LoginAttemptFailureEventListener {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private AuthenticationBlockingService authenticationBlockingService;


    public LoginAttemptFailureEventListener(AuthenticationBlockingService service) {
        this.authenticationBlockingService = service;
    }

    @EventListener
    public void handle(AuthenticationFailureBadCredentialsEvent event) {
        String login = event.getAuthentication().getName();
        LOG.info("Handling login attempts after failed authentication for {}", login);
        if (StringUtils.isNotBlank(login)) {
            authenticationBlockingService.registerAttemptToLogin(login);
        }
    }
}
