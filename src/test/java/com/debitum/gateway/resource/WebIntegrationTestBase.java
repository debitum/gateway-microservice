package com.debitum.gateway.resource;

import com.debitum.gateway.IntegrationTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;


@AutoConfigureMockMvc
public abstract class WebIntegrationTestBase extends IntegrationTestBase {

    @Inject
    protected ObjectMapper objectMapper;

    @Inject
    protected MockMvc mockMvc;

    protected static class OauthResponse {
        private String access_token;
        private String refresh_token;

        public OauthResponse(String access_token, String refresh_token) {
            this.access_token = access_token;
            this.refresh_token = refresh_token;
        }

        public OauthResponse() {
        }

        public String getAccess_token() {
            return access_token;
        }

        public String getRefresh_token() {
            return refresh_token;
        }

    }


}