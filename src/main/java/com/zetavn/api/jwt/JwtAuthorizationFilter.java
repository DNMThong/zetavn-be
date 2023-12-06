package com.zetavn.api.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetavn.api.exception.CustomExceptionHandler;
import com.zetavn.api.model.entity.RefreshTokenEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.repository.RefreshTokenRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.RefreshTokenService;
import com.zetavn.api.service.impl.RefreshTokenImpl;
import com.zetavn.api.utils.JwtHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;

    private final RefreshTokenService refreshTokenService;

    private  final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

//        log.info("authorizationHeader: " + authorizationHeader);
//        log.info("Method: {}, URI: {}",request.getMethod(),request.getRequestURI());

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                DecodedJWT decodedJWT = jwtHelper.decodedJWT(token);
                if(decodedJWT.getExpiresAt().before(new Date()))
                    throw new TokenExpiredException("The token was expired", decodedJWT.getExpiresAt().toInstant());
                String username = decodedJWT.getSubject();
                UserEntity user = userRepository.findUserEntityByEmail(username);

                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user.getUserId(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (TokenExpiredException e) {
                log.error("Error logging in: {}", e.getMessage());
                response.setHeader("Error", "The Token is invalid");
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", "TokenInvalid");
                error.put("message", "The Token is invalid");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            } catch (SignatureVerificationException e) {
                log.error("Error logging in: {}", e.getMessage());
                response.setHeader("Error", "The Token is invalid");
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", "TokenInvalid");
                error.put("message", "The Token is invalid");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            } catch (Exception e) {
                log.error("Error logging in: {}", e.getMessage());
                response.setHeader("Error", "The Token is invalid");
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", "TokenInvalid");
                error.put("message", "The Token is invalid");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }

        }
        filterChain.doFilter(request, response);
    }
}