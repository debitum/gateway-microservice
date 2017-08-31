package com.debitum.gateway.port.adapter.security;


import com.debitum.gateway.domain.model.security.AuthenticationService;
import com.debitum.gateway.domain.model.user.User;
import com.debitum.gateway.domain.model.user.UserAuthDetails;
import com.debitum.gateway.port.adapter.application.SpringBootApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
class TwoFactorAuthentication implements AuthenticationService {
    private final Logger log = LoggerFactory.getLogger(TwoFactorAuthentication.class);

    private UserAuthServiceIntegration userAuthServiceIntegration;
    private TokenStore tokenStore;
    private SpringBootApplicationProperties springBootApplicationProperties;

    @Autowired
    TwoFactorAuthentication(UserAuthServiceIntegration userAuthServiceIntegration,
                            TokenStore tokenStore,
                            SpringBootApplicationProperties springBootApplicationProperties) {
        this.userAuthServiceIntegration = userAuthServiceIntegration;
        this.tokenStore = tokenStore;
        this.springBootApplicationProperties = springBootApplicationProperties;
    }

    @Override
    public void refreshUserDetails() {

        User user = userAuthServiceIntegration.findOneByLogin(SecurityUtils.getCurrentUser().getLogin());
        addAuthorities(user);

    }

    @Override
    public void refreshUserDetails(String usersLogin) {
        User user = userAuthServiceIntegration.findOneByLogin(usersLogin);
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(springBootApplicationProperties.getOauthClient(), usersLogin);

        tokens.stream().forEach(token -> {
            List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.toString()))
                    .collect(Collectors.toList());
            UserAuthDetails targetUser = new UserAuthDetails(
                    user.getId(),
                    user.getLogin(),
                    user.getPhone(),
                    user.getCompany(),
                    user.getName(),
                    user.getPassword(),
                    user.getStatus(),
                    grantedAuthorities
            );
            OAuth2Authentication authentication = tokenStore.readAuthentication(token);
            OAuth2Authentication newAuthentication =
                    new OAuth2Authentication(authentication.getOAuth2Request(), new UsernamePasswordAuthenticationToken(
                            targetUser,
                            authentication.getCredentials(),
                            grantedAuthorities));
            tokenStore.storeAccessToken(token, newAuthentication);
        });

    }

    private void addAuthorities(User user) {
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.toString()))
                .collect(Collectors.toList());

        UserAuthDetails targetUser = new UserAuthDetails(
                user.getId(),
                user.getLogin(),
                user.getPhone(),
                user.getCompany(),
                user.getName(),
                user.getPassword(),
                user.getStatus(),
                grantedAuthorities
        );

        OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();

        OAuth2Authentication newAuthentication =
                new OAuth2Authentication(authentication.getOAuth2Request(), new UsernamePasswordAuthenticationToken(
                        targetUser,
                        authentication.getCredentials(),
                        grantedAuthorities));

        OAuth2AccessToken accessToken = tokenStore.getAccessToken(authentication);

        tokenStore.storeAccessToken(accessToken, newAuthentication);
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }


}
