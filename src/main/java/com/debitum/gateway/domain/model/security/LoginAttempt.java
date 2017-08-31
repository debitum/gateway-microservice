package com.debitum.gateway.domain.model.security;


import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "LOGIN_ATTEMPT")
public class LoginAttempt {


    public static final int MAX_ALLOWED_LOGIN_ATTEMPTS = 5;

    public static final int BAN_DURATION_IN_MINUTES = 5;

    @Id
    @NotBlank
    private String login;

    private int attempts;

    @Column(name = "LAST_LOGIN_DATE_TIME")
    private Instant lastLoginDateTime;

    LoginAttempt() {
    }

    public LoginAttempt(String login) {
        this.login = login.toLowerCase();
        this.attempts = 1;
        lastLoginDateTime = Instant.now();
    }

    public String getLogin() {
        return login;
    }

    public int getAttempts() {
        return attempts;
    }

    public void addAttemptToLogin() {
        this.attempts++;
        lastLoginDateTime = Instant.now();
    }


    public Instant getLastLoginTime() {
        return lastLoginDateTime;
    }

    public void reset() {
        this.attempts = 0;
    }

    public boolean shouldBeBlocked() {
        return (getAttempts() >= MAX_ALLOWED_LOGIN_ATTEMPTS) &&
                getLastLoginTime().isAfter(Instant.now().minus(BAN_DURATION_IN_MINUTES, ChronoUnit.MINUTES));
    }
}
