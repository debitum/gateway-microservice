package com.debitum.gateway.resource;


import com.debitum.gateway.domain.model.user.UserStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.UUID;


@ApiModel(
        value = "UserDTO",
        description = "User's resource"
)
public class UserDTO {

    @ApiModelProperty(value = "User's identifier")
    private UUID id;

    @ApiModelProperty(value = "User's login")
    private String login;

    @ApiModelProperty(value = "User's phone number")
    private String phone;

    @ApiModelProperty(value = "User's company")
    private String company;

    @ApiModelProperty(value = "User's fullname")
    private String name;

    @ApiModelProperty(value = "User's status")
    private UserStatus status;

    @ApiModelProperty(value = "User's authorities")
    private List<String> authorities;


    public UserDTO(UUID id,
                   String login,
                   String phone,
                   String company,
                   String name,
                   UserStatus status,
                   List<String> authorities) {
        this.id = id;
        this.login = login;
        this.phone = phone;
        this.company = company;
        this.name = name;
        this.status = status;
        this.authorities = authorities;
    }


    public UserDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }


    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", phone='" + phone + '\'' +
                ", company='" + company + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", authorities=" + authorities +
                '}';
    }
}
