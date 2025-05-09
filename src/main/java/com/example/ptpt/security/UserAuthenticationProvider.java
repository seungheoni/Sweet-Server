package com.example.ptpt.security;

import com.example.ptpt.delegator.AuthenticationDelegator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationDelegator authenticationDelegator;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        try {
            authenticationDelegator.restAuth(email, password);
        } catch (Exception e ) {
            throw new RuntimeException("인증 실패!");
        };

        return new UserAuthentication(email, password);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserAuthentication.class.isAssignableFrom(authentication);
    }
}
