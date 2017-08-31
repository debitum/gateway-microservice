package com.debitum.gateway.domain.model.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.UUID;

public class UserAuthDetails extends User {

    private UUID id;
    private String login;
    private String phone;
    private String company;
    private String name;
    private UserStatus status;

    public UserAuthDetails(UUID id,
                           String login,
                           String phone,
                           String company,
                           String name,
                           String password,
                           UserStatus status,
                           List<GrantedAuthority> grantedAuthorities) {
        super(login, password, true, true, true, true, grantedAuthorities);
        this.id = id;
        this.login = login;
        this.phone = phone;
        this.company = company;
        this.name = name;
        this.status = status;

    }

    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPhone() {
        return phone;
    }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public UserStatus getStatus() {
        return status;
    }

}
