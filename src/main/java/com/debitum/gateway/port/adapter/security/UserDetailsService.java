package com.debitum.gateway.port.adapter.security;


import com.debitum.gateway.domain.model.user.User;
import com.debitum.gateway.domain.model.user.UserAuthDetails;
import com.debitum.gateway.domain.model.user.UserStatus;
import com.debitum.gateway.resource.ErrorCodedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Component
class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    private UserAuthServiceIntegration userAuthServiceIntegration;
    private HttpServletRequest request;

    private AuthenticationBlockingService blockingService;

    UserDetailsService(UserAuthServiceIntegration userAuthServiceIntegration,
                       HttpServletRequest request,
                       AuthenticationBlockingService blockingService) {
        this.userAuthServiceIntegration = userAuthServiceIntegration;
        this.request = request;
        this.blockingService = blockingService;
    }

    @Override
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase();
        blockingService.blockUserIfNeeded(lowercaseLogin);
        try {
            User user = userAuthServiceIntegration.findOneByLogin(lowercaseLogin);
            if (user != null) {

                if (user.getStatus() == UserStatus.INACTIVE || user.getStatus() == UserStatus.PENDING) {
                    throw new BadCredentialsException("ACCESS_DENIED_USER_INACTIVE");
                }


                return new UserAuthDetails(
                        user.getId(),
                        user.getLogin(),
                        user.getPhone(),
                        user.getCompany(),
                        user.getName(),
                        user.getPassword(),
                        user.getStatus(),
                        collectGrantedAuthorities(user)
                );
            }
        } catch (ErrorCodedException ex) {
            log.warn("Got authentication error {}", ex.getMessage());
        }

        throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " +
                "database");
    }


    /**
     * Collecting authorities by user:
     * giving authorities if user has valid cookie and giving empty authorities list if he doesn't have valid cookie.
     *
     * @param user - loaded user by it's username
     * @return list of granted authorities
     */
    private List<GrantedAuthority> collectGrantedAuthorities(User user) {
        return user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.toString()))
                .collect(Collectors.toList());
    }
}
