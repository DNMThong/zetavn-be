package com.zetavn.api.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zetavn.api.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtHelper {
    @Value("${zetavn.secret_key}")
    private String ZETA_SECRET_KEY ;
    @Value("${zetavn.secret_key_ref}")
    private String ZETA_SECRET_KEY_REFRESH;

    @Value("${zetavn.secret_key_reset_password}")
    private String ZETA_SECRET_KEY_RESET_PASSWORD;


    @Value("${zetavn.access_token_expiration_time}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${zetavn.refresh_token_expiration_time}")
    private Long REFRESH_TOKEN_EXPIRATION_TIME;

    @Value("${zetavn.secret_key_reset_password_time}")
    private Long RESET_PASSWORD_TOKEN_EXPIRATION_TIME;


    public Map<String, String> generateTokens(UserEntity user) {
        String access_token = generateToken(user);

        String refresh_token = generateRefreshToken(user);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        return tokens;
    }

    public String generateToken(UserEntity user) {
        ArrayList<String> authorities = new ArrayList<>();
        authorities.add(user.getRole().name());
        Algorithm algorithm = getAlgorithm();
        String access_token = JWT.create()
                .withSubject(user.getEmail())
                .withClaim("status", user.getStatus().toString())
                .withClaim("roles", authorities)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(algorithm);
        return access_token;
    }

    public String generateTokenResetPassword(UserEntity user) {
        ArrayList<String> authorities = new ArrayList<>();
        authorities.add(user.getRole().name());
        Algorithm algorithm = getAlgorithm_resetPassword();
        String access_token = JWT.create()
                .withSubject(user.getEmail())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + RESET_PASSWORD_TOKEN_EXPIRATION_TIME))
                .sign(algorithm);
        return access_token;
    }

    public String generateRefreshToken(UserEntity user) {

        Algorithm algorithm = getAlgorithm_refresh();
        String refresh_token = JWT.create()
                .withSubject(user.getEmail())
                .withClaim("userId", user.getUserId())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .sign(algorithm);
        return refresh_token;
    }

    private JWTVerifier verifier(Algorithm algorithm) {
        return JWT.require(algorithm).build();
    }

    public DecodedJWT decodedJWT(String token) {
        return verifier(getAlgorithm()).verify(token);
    }

    public DecodedJWT decodedJWTRef(String token) {
        return verifier(getAlgorithm_refresh()).verify(token);
    }

    public DecodedJWT decodedTokenResetPassword(String token) {
        return verifier(getAlgorithm_resetPassword()).verify(token);
    }

    private Algorithm getAlgorithm_refresh() {
        return Algorithm.HMAC256(ZETA_SECRET_KEY_REFRESH.getBytes());
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(ZETA_SECRET_KEY.getBytes());
    }

    private Algorithm getAlgorithm_resetPassword() {
        return Algorithm.HMAC256(ZETA_SECRET_KEY_RESET_PASSWORD.getBytes());
    }
}
