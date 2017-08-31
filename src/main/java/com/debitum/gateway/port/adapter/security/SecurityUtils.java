package com.debitum.gateway.port.adapter.security;


import com.debitum.gateway.domain.model.user.UserAuthDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    public static UserAuthDetails getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserAuthDetails) {
                return (UserAuthDetails) authentication.getPrincipal();
            }
        }
        return null;
    }
}
