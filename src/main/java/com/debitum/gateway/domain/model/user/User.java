package com.debitum.gateway.domain.model.user;

import java.util.Set;
import java.util.UUID;

public class User {

    private UUID id;
    private String login;
    private String phone;
    private String company;
    private String name;
    private String password;
    private UserStatus status;
    private Set<String> authorities;

    public User() {
    }

    private User(UUID id,
                 String login,
                 String phone,
                 String company,
                 String name,
                 String password,
                 UserStatus status,
                 Set<String> authorities) {
        this.id = id;
        this.login = login;
        this.phone = phone;
        this.company = company;
        this.name = name;
        this.password = password;
        this.status = status;
        this.authorities = authorities;
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

    public String getPassword() {
        return password;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }


    public static class UserBuilder {

        private UUID id;
        private String login;
        private String phone;
        private String company;
        private String name;
        private String password;
        private UserStatus status;
        private Set<String> authorities;

        public UserBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserBuilder login(String login) {
            this.login = login;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserBuilder company(String company) {
            this.company = company;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public UserBuilder authorities(Set<String> authorities) {
            this.authorities = authorities;
            return this;
        }


        public User build() {
            return new User(id,
                    login,
                    phone,
                    company,
                    name,
                    password,
                    status,
                    authorities);
        }
    }
}
