package com.debitum.gateway;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.context.annotation.Configuration;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Configuration
public class ExternalSourcesStubServer {

    public static final String WIREMOCK_HOST = "localhost";
    public static final int WIREMOCK_PORT = 9999;


    public static WireMockConfiguration getDefaultConfig() {
        return wireMockConfig().port(WIREMOCK_PORT).bindAddress(WIREMOCK_HOST);
    }


}
