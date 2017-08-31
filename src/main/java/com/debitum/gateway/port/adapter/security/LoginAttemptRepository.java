package com.debitum.gateway.port.adapter.security;


import com.debitum.gateway.domain.model.security.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface LoginAttemptRepository extends JpaRepository<LoginAttempt, String>, JpaSpecificationExecutor {
}
