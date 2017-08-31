package com.debitum.gateway.application.security;


import com.debitum.gateway.domain.model.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationApplicationService {
    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationApplicationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void refreshUserDetails(String usersLogin){
        authenticationService.refreshUserDetails(usersLogin);
    }

    public void refreshUserDetails(){
        authenticationService.refreshUserDetails();
    }
}
