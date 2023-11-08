package com.zetavn.api.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JWTAuthenticationToken extends AbstractAuthenticationToken implements Authentication {
    private String token;
    private UserDetails principal;

    public JWTAuthenticationToken(Collection<? extends GrantedAuthority> authorities, String token, UserDetails principal) {
        super(authorities);
        this.token =  token;
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
