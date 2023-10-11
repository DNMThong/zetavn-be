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
import com.zetavn.api.repository.RefreshTokenRepository;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.DateTimeException;
import java.util.*;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;

    private final RefreshTokenService refreshTokenService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/v0/auth/login")) {
            filterChain.doFilter(request, response);
        }
        else if (request.getServletPath().equals("/api/v0/auth/token/refresh")) {
            String refreshToken = null;
            Cookie[] cookies = request.getCookies();
            if(cookies != null && cookies.length > 0) {
                for (Cookie c : cookies) {
                    if (c.getName().equals("refresh_token2")) {
                        refreshToken = c.getValue();
                        log.info("ref: {}", refreshToken);
                    }
                }
            }
            if (refreshToken == null || refreshToken.equals("") ) {
//                refreshTokenService.removeRefreshTokenByIpAddressAndUserId("12343433", "86899319-9133-47f4-9051-439a65075ecc");
                response.setHeader("ERROR", "Unauthorized");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                Map<String, String> error = new HashMap<>();
                error.put("message", "Unauthorized");
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            } else {
                RefreshTokenEntity refreshTokenEntity = refreshTokenService.getRefreshTokenByToken(refreshToken);
                String rt_db = refreshTokenEntity.getToken();
                try {
                    DecodedJWT decodedJWT = jwtHelper.decodedJWTRef(refreshToken);
                    String username = decodedJWT.getSubject();
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (TokenExpiredException e) {
                    log.error("Error logging in: {}", e.getMessage());
                    response.setHeader("ERROR", e.getMessage());
                    response.setStatus(UNAUTHORIZED.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "TokenExpired");
                    error.put("message", e.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                } catch (SignatureVerificationException e) {
                    log.error("Error logging in: {}", e.getMessage());
                    response.setHeader("ERROR", e.getMessage());
                    response.setStatus(UNAUTHORIZED.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "TokenInvalid");
                    error.put("message", "The token is invalid");
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                } catch (Exception e) {
                    log.error("Error logging in: {}", e.getMessage());
                    response.setHeader("ERROR", e.getMessage());
                    response.setStatus(UNAUTHORIZED.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error", e.getMessage());
                    error.put("message", e.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            log.info("authorizationHeader: " + authorizationHeader);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    DecodedJWT decodedJWT = jwtHelper.decodedJWT(token);
                    if(decodedJWT.getExpiresAt().before(new Date()))
                        throw new TokenExpiredException("The token was expired", decodedJWT.getExpiresAt().toInstant());
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (TokenExpiredException e) {
                    log.error("Error logging in: {}", e.getMessage());
                    response.setHeader("ERROR", e.getMessage());
                    response.setStatus(UNAUTHORIZED.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "TokenExpired");
                    error.put("message", e.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                } catch (SignatureVerificationException e) {
                    log.error("Error logging in: {}", e.getMessage());
                    response.setHeader("ERROR", e.getMessage());
                    response.setStatus(UNAUTHORIZED.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "SignatureInvalid");
                    error.put("message", "The Token is invalid");
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                } catch (Exception e) {
                    log.error("Error logging in: {}", e.getMessage());
                    response.setHeader("ERROR", e.getMessage());
                    response.setStatus(UNAUTHORIZED.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error", e.getMessage());
                    error.put("message", e.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }

            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}