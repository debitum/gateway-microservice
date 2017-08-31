package com.debitum.gateway.port.adapter.security;


import com.debitum.gateway.domain.model.security.LoginAttempt;
import com.debitum.gateway.domain.model.security.exception.MaximumLoginAttemptsReachedException;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
class AuthenticationBlockingService {

    private final LoginAttemptRepository loginAttemptRepository;

    AuthenticationBlockingService(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LoginAttempt registerAttemptToLogin(@NotBlank String login) {
        LoginAttempt loginAttempt = loginAttemptRepository.findOne(login.toLowerCase());
        if (loginAttempt == null) {
            loginAttempt = new LoginAttempt(login);
        } else {
            loginAttempt.addAttemptToLogin();
        }
        return loginAttemptRepository.save(loginAttempt);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void reset(@NotBlank String login) {
        if (loginAttemptRepository.exists(login.toLowerCase())) {
            loginAttemptRepository.delete(login.toLowerCase());
        }
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public void blockUserIfNeeded(@NotBlank String login) {
        Optional.ofNullable(loginAttemptRepository.findOne(login.toLowerCase()))
                .filter(loginAttempt -> loginAttempt.shouldBeBlocked())
                .ifPresent((LoginAttempt t) -> {
                    throw new MaximumLoginAttemptsReachedException();
                });
    }
}
