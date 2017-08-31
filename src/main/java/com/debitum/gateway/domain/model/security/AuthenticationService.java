package com.debitum.gateway.domain.model.security;


public interface AuthenticationService {

    void refreshUserDetails(String usersLogin);
    void refreshUserDetails();

}
