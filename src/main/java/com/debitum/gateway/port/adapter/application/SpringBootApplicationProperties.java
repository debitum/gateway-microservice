package com.debitum.gateway.port.adapter.application;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Spring boot application properties.
 */
@Component
public class SpringBootApplicationProperties {

    @Value("${authentication.oauth.clientid}")
    private String oauthClient;

    public String getOauthClient() {
        return oauthClient;
    }
}
