package com.debitum.gateway.domain.model.user;


public enum UserStatus {

    /**
     * Active user.
     */
    ACTIVE,
    /**
     * Not yet activated user.
     */
    PENDING,
    /**
     * User deactivated by an admin.
     */
    INACTIVE
}
