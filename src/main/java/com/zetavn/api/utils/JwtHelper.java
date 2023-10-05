package com.zetavn.api.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.zetavn.api.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtHelper {
    @Value("${zetavn.secret_key}")
    private String ZETA_SECRET_KEY;

    @Value("${zetavn.access_token_expiration_time}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${zetavn.refresh_token_expiration_time}")
    private Long REFRESH_TOKEN_EXPIRATION_TIME;

    public Map<String, String> generateTokens(UserEntity user) {
        Algorithm algorithm = Algorithm.HMAC256(ZETA_SECRET_KEY.getBytes());
        String access_token = JWT.create()
                .withSubject(user.getEmail())
                .withClaim("status", user.getStatus().toString())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(algorithm);

        String refresh_token = JWT.create()
                .withSubject(user.getEmail())
                .withClaim("userId", user.getUserId())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .sign(algorithm);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        return tokens;
    }

    public String generateToken(UserEntity user) {
        Algorithm algorithm = Algorithm.HMAC256(ZETA_SECRET_KEY.getBytes());
        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withClaim("status", user.getStatus().toString())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))

                .sign(algorithm);
        return access_token;
    }

    public String generateRefreshToken(UserEntity user) {
        Algorithm algorithm = Algorithm.HMAC256(ZETA_SECRET_KEY.getBytes());
        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withClaim("userId", user.getUserId())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .sign(algorithm);
        return refresh_token;
    }
}
