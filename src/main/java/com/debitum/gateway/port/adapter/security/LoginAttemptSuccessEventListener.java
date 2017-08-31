package com.debitum.gateway.port.adapter.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
class LoginAttemptSuccessEventListener {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private AuthenticationBlockingService loginAttemptComponent;

    public LoginAttemptSuccessEventListener(AuthenticationBlockingService loginAttemptComponent) {
        this.loginAttemptComponent = loginAttemptComponent;
    }

    @EventListener
    public void handle(AuthenticationSuccessEvent event) {
        String login = event.getAuthentication().getName();
        LOG.debug("Handling login attempts after successful authentication for {}", login);
        if (StringUtils.isNotBlank(login)) {
            loginAttemptComponent.reset(login);
        }
    }
}
