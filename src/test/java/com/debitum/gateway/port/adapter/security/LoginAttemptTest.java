package com.debitum.gateway.port.adapter.security;


import com.debitum.gateway.domain.model.security.LoginAttempt;
import com.debitum.gateway.domain.model.security.exception.MaximumLoginAttemptsReachedException;
import com.debitum.gateway.domain.model.user.User;
import com.debitum.gateway.domain.model.user.UserStatus;
import com.debitum.gateway.resource.ErrorDTO;
import com.debitum.gateway.resource.WebIntegrationTestBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.debitum.gateway.domain.model.security.LoginAttempt.BAN_DURATION_IN_MINUTES;
import static com.debitum.gateway.domain.model.security.LoginAttempt.MAX_ALLOWED_LOGIN_ATTEMPTS;
import static com.debitum.gateway.domain.model.security.exception.MaximumLoginAttemptsReachedException.MESSAGE;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginAttemptTest extends WebIntegrationTestBase {

    private final static User EXISTING_ADMIN_USER;
    private static final UUID EXISTING_ADMIN_USER_ID = UUID.fromString("8180d9a4-7bfc-11e7-bb31-be2e44b06b34");
    private static final String EXISTING_ADMIN_USER_LOGIN = "test_existing_user_login";
    private static final String EXISTING_ADMIN_USER_PASSWORD = "qwerty";
    private static final String ENCRYPTED_EXISTING_ADMIN_USER_PASSWORD = new BCryptPasswordEncoder().encode(EXISTING_ADMIN_USER_PASSWORD);

    private static final String NOT_EXISTING_USERS_LOGIN = "some_not_existing_users_login";
    private static final String NOT_EXISTING_USERS_PASSWORD = "Some_Not_existing_users_password";

    @Inject
    private LoginAttemptRepository loginAttemptRepository;

    static {
        EXISTING_ADMIN_USER = new User.UserBuilder()
                .id(EXISTING_ADMIN_USER_ID)
                .login(EXISTING_ADMIN_USER_LOGIN)
                .phone("370")
                .company("TEST_COMPANY")
                .name("TEST_NAME")
                .password(ENCRYPTED_EXISTING_ADMIN_USER_PASSWORD)
                .status(UserStatus.ACTIVE)
                .authorities(ImmutableSet.of("ROLE_USERS_EDIT"))
                .build();
    }

    @Before
    public void setup() throws JsonProcessingException {
        stubFor(WireMock.get(urlPathEqualTo("/assets/api/private/login/" + EXISTING_ADMIN_USER_LOGIN.toLowerCase() + "/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(objectMapper.writeValueAsString(EXISTING_ADMIN_USER))));

        stubFor(WireMock.get(urlMatching("/assets/api/private/login/" + NOT_EXISTING_USERS_LOGIN.toLowerCase() + "/"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(objectMapper.writeValueAsString(new ErrorDTO("Bad request")))));


    }

    @Test
    public void givenExistingLowercaseLoginWithMaximumAttemptsAndActiveBanDuration_whenGettingShouldBeBlocked_thenReturnTrue() throws Exception {
        // given
        String login = "debitum@login.com";
        Instant lastLoginDateTime = Instant.now().minus(BAN_DURATION_IN_MINUTES, ChronoUnit.MINUTES).plus(1, ChronoUnit.MINUTES);
        LoginAttempt existingAttempt = new LoginAttempt(login) {

            @Override
            public Instant getLastLoginTime() {
                return lastLoginDateTime;
            }

            @Override
            public int getAttempts() {
                return MAX_ALLOWED_LOGIN_ATTEMPTS;
            }
        };

        // when
        boolean result = existingAttempt.shouldBeBlocked();

        // then
        AssertionsForClassTypes.assertThat(result).isTrue();
    }

    @Test
    public void givenExistingLowercaseLoginWithMaximumAttemptsAndNotactiveBanDuration_whenGettingShouldBeBlocked_thenReturnFalse() throws Exception {
        // given
        String login = "debitum@login.com";
        Instant lastLoginDateTime = Instant.now().minus(BAN_DURATION_IN_MINUTES, ChronoUnit.MINUTES);

        LoginAttempt existingAttempt = new LoginAttempt(login) {

            @Override
            public Instant getLastLoginTime() {
                return lastLoginDateTime;
            }

            @Override
            public int getAttempts() {
                return MAX_ALLOWED_LOGIN_ATTEMPTS;
            }
        };

        // when
        boolean result = existingAttempt.shouldBeBlocked();

        // then
        AssertionsForClassTypes.assertThat(result).isFalse();
    }


    @Test
    public void givenUserWithLoginExists_whenAttemptingLoginMultipleTimesWithInvalidCredentials_thenAttemptsAreLoggedAndResetAfterSuccessfulLogin() throws Exception {
        // given
        String login = EXISTING_ADMIN_USER_LOGIN;
        String password = EXISTING_ADMIN_USER_PASSWORD;


        // when
        authenticate(login, password + "1", HttpStatus.BAD_REQUEST);

        // then
        assertThat(loginAttemptRepository.getOne(login).getAttempts()).isEqualTo(1);

        // when
        authenticate(login, password + "2", HttpStatus.BAD_REQUEST);

        // then
        assertThat(loginAttemptRepository.getOne(login).getAttempts()).isEqualTo(2);

        // when
        authenticate(login, password, HttpStatus.OK);

        // then
        assertThat(loginAttemptRepository.findOne(login)).isNull();
    }

    @Test
    public void givenUserDoesNotExist_whenAttemptingToLogin_thenAlwaysWillGetBadRequestResponse() throws Exception {
        // given
        String login = NOT_EXISTING_USERS_LOGIN;
        String password = NOT_EXISTING_USERS_PASSWORD;

        // when
        authenticate(login, password + "1", HttpStatus.BAD_REQUEST);

        // then
        assertThat(loginAttemptRepository.getOne(login).getAttempts()).isEqualTo(1);

        // when
        authenticate(login, password + "2", HttpStatus.BAD_REQUEST);

        // then
        assertThat(loginAttemptRepository.getOne(login).getAttempts()).isEqualTo(2);

        // when
        authenticate(login, password, HttpStatus.BAD_REQUEST);

        // then
        assertThat(loginAttemptRepository.getOne(login).getAttempts()).isEqualTo(3);
    }

    @Test
    public void givenLogin_whenAttemptingToLoginMaximumAllowedTimes_thenLoginIsBlocked() throws Exception {
        // given
        String login = EXISTING_ADMIN_USER_LOGIN;
        String password = EXISTING_ADMIN_USER_PASSWORD;


        // and
        String expectedResponse = objectMapper.writeValueAsString(
                new OauthUnauthorizedException(MESSAGE, new Exception()));

        // when
        for (int i = 0; i < MAX_ALLOWED_LOGIN_ATTEMPTS; i++) {
            authenticate(login, password + "1", HttpStatus.BAD_REQUEST);
        }

        // and
        String response = authenticate(login, password, HttpStatus.UNAUTHORIZED);

        // then
        assertThat(response).isEqualTo(expectedResponse);

        // when trying again with the correct password
        response = authenticate(login, password, HttpStatus.UNAUTHORIZED);

        // then response is still unauthorized since the login is banned
        assertThat(response).isEqualTo(expectedResponse);
    }

    private static class OauthUnauthorizedException extends OAuth2Exception {

        public OauthUnauthorizedException(String msg, Throwable t) {
            super(msg, t);
        }

        public String getOAuth2ErrorCode() {
            return "unauthorized";
        }

        public int getHttpErrorCode() {
            return 401;
        }

    }

    private String authenticate(String login, String password, HttpStatus expectedStatus) throws Exception {
        String response = mockMvc.perform(post("/oauth/token")
                .header("Authorization", "Basic ZGViaXR1bWFwcDp4NThHVUtOOFRRSEIzRkc=")
                .content("username=" + login + "&password=" + password + "&grant_type=password")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(status().is(expectedStatus.value()))
                .andReturn().getResponse().getContentAsString();
        em.flush();
        return response;
    }


}
