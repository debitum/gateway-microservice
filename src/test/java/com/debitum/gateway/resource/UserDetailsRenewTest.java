package com.debitum.gateway.resource;


import com.debitum.gateway.domain.model.security.AuthenticationService;
import com.debitum.gateway.domain.model.user.User;
import com.debitum.gateway.domain.model.user.UserStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.inject.Inject;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserDetailsRenewTest extends WebIntegrationTestBase {

    private final static User EXISTING_USER;
    private final static User EXISTING_CHANGED_USER;

    private static final UUID EXISTING_USER_ID = UUID.fromString("f954f046-7bfc-11e7-bb31-be2e44b06b34");
    private static final String EXISTING_USER_LOGIN = "test_existing_user_login";

    private static final String EXISTING_USER_PASSWORD = "qwerty";
    private static final String ENCRYPTED_EXISTING_USER_PASSWORD = new BCryptPasswordEncoder().encode(EXISTING_USER_PASSWORD);

    @Inject
    private AuthenticationService authenticationService;

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
        EXISTING_CHANGED_USER = new User.UserBuilder()
                .id(EXISTING_USER_ID)
                .login(EXISTING_USER_LOGIN)
                .phone("370")
                .company("TEST_COMPANY")
                .name("TEST_NAME_CHANGED")
                .password(ENCRYPTED_EXISTING_USER_PASSWORD)
                .status(UserStatus.ACTIVE)
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

    }

    @Test
    public void givenExistingUser_whenAuthenticationServiceGetsNotificationAboutNeedOfRenewUserDetails_thenUserDetailsIsRefresh() throws Exception {
        //given
        String response = mockMvc.perform(post("/oauth/token")
                .header("Authorization", "Basic ZGViaXR1bWFwcDp4NThHVUtOOFRRSEIzRkc=")
                .content("username=" + EXISTING_USER_LOGIN + "&password=" + EXISTING_USER_PASSWORD + "&grant_type=password")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        OauthResponse oauthResponse = objectMapper.readValue(response, OauthResponse.class);

        //when
        UserDTO authenticatedUsers = getCurrentUser(oauthResponse);

        //then
        assertThat(authenticatedUsers).isNotNull();
        assertThat(authenticatedUsers.getName()).isEqualTo("TEST_NAME");

        //when
        stubFor(WireMock.get(urlPathEqualTo("/users/api/private/login/" + EXISTING_USER_LOGIN.toLowerCase() + "/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(objectMapper.writeValueAsString(EXISTING_CHANGED_USER))));

        authenticationService.refreshUserDetails(EXISTING_USER_LOGIN);
        authenticatedUsers = getCurrentUser(oauthResponse);

        //then
        assertThat(authenticatedUsers).isNotNull();
        assertThat(authenticatedUsers.getName()).isEqualTo("TEST_NAME");

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
