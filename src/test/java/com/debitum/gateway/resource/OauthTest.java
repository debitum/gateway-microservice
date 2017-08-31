package com.debitum.gateway.resource;

import com.debitum.gateway.domain.model.user.User;
import com.debitum.gateway.domain.model.user.UserStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OauthTest extends WebIntegrationTestBase {

    private final static User EXISTING_USER;
    private final static User EXISTING_INACTIVE_USER;
    private static final UUID EXISTING_USER_ID = UUID.fromString("8180d9a4-7bfc-11e7-bb31-be2e44b06b34");
    private static final String EXISTING_USER_LOGIN = "TEST_EXISTING_USER_LOGIN";
    private static final String EXISTING_INACTIVE_USER_LOGIN = "TEST_EXISTING_INACTIVE_USER_LOGIN";
    private static final String EXISTING_USER_PASSWORD = "qwerty";
    private static final String ENCRYPTED_EXISTING_USER_PASSWORD = new BCryptPasswordEncoder().encode(EXISTING_USER_PASSWORD);

    static {
        EXISTING_USER = new User.UserBuilder()
                .id(EXISTING_USER_ID)
                .login(EXISTING_USER_LOGIN)
                .phone("370")
                .company("TEST_COMPANY")
                .name("TEST_NAME")
                .password(ENCRYPTED_EXISTING_USER_PASSWORD)
                .status(UserStatus.ACTIVE)
                .authorities(ImmutableSet.of("ROLE_ASSETS_VIEW"))
                .build();
        EXISTING_INACTIVE_USER = new User.UserBuilder()
                .id(EXISTING_USER_ID)
                .login(EXISTING_USER_LOGIN)
                .phone("370")
                .company("TEST_COMPANY")
                .name("TEST_NAME")
                .password(ENCRYPTED_EXISTING_USER_PASSWORD)
                .status(UserStatus.INACTIVE)
                .authorities(ImmutableSet.of("ROLE_ASSETS_VIEW"))
                .build();
    }

    @Before
    public void setup() throws JsonProcessingException {
        stubFor(WireMock.get(urlPathEqualTo("/assets/api/private/login/" + EXISTING_USER_LOGIN.toLowerCase() + "/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(objectMapper.writeValueAsString(EXISTING_USER))));

        stubFor(WireMock.get(urlPathEqualTo("/assets/api/private/login/" + EXISTING_INACTIVE_USER_LOGIN.toLowerCase() + "/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(objectMapper.writeValueAsString(EXISTING_INACTIVE_USER))));
    }

    @Test
    public void givenExistingUser_whenUserLoginsWithRightCredentials_thenUserAuthenticates() throws Exception {
        String response = mockMvc.perform(post("/oauth/token")
                .header("Authorization", "Basic ZGViaXR1bWFwcDp4NThHVUtOOFRRSEIzRkc=")
                .content("username=" + EXISTING_USER_LOGIN + "&password=" + EXISTING_USER_PASSWORD + "&grant_type=password")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        OauthResponse oauthResponse = objectMapper.readValue(response, OauthResponse.class);

        UserDTO authenticatedUsers = getCurrentUser(oauthResponse);

        assertThat(authenticatedUsers).isNotNull();
        assertThat(authenticatedUsers.getLogin()).isEqualTo(EXISTING_USER_LOGIN);
    }

    @Test
    public void givenExistingUser_whenUserLoginsWithRightCredentialsGivingAuthorizationSecretThroughUrl_thenUserAuthenticates() throws Exception {
        String response = mockMvc.perform(post("/oauth/token?username=" + EXISTING_USER_LOGIN + "&password=" + EXISTING_USER_PASSWORD + "&grant_type=password&client_id=debitumapp&client_secret=x58GUKN8TQHB3FG")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        OauthResponse oauthResponse = objectMapper.readValue(response, OauthResponse.class);

        UserDTO authenticatedUsers = getCurrentUser(oauthResponse);

        assertThat(authenticatedUsers).isNotNull();
        assertThat(authenticatedUsers.getLogin()).isEqualTo(EXISTING_USER_LOGIN);
    }

    @Test
    public void givenInactiveExistingUser_whenUserLoginsWithRightCredentials_thenUserGetsBadCredentialsException() throws Exception {
        String response = mockMvc.perform(post("/oauth/token")
                .header("Authorization", "Basic ZGViaXR1bWFwcDp4NThHVUtOOFRRSEIzRkc=")
                .content("username=" + EXISTING_INACTIVE_USER_LOGIN + "&password=" + EXISTING_USER_PASSWORD + "&grant_type=password")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();


        Map<String, String> errorResponse = objectMapper.readValue(response, HashMap.class);


        assertThat(errorResponse.get("error_description")).isEqualTo("ACCESS_DENIED_USER_INACTIVE");
    }

    private UserDTO getCurrentUser(OauthResponse oauthResponse) throws Exception {
        String retrievedAccountJson = mockMvc.perform(get("/account")
                .header("Authorization", "Bearer " + oauthResponse.getAccess_token())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        status()
                                .isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(retrievedAccountJson, UserDTO.class);
    }

    private static class OauthResponse {
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
