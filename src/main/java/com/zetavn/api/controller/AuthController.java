package com.zetavn.api.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.request.SignInRequest;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.JwtResponse;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v0/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;
//
    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${zetavn.secret_key}")
    private String ZETAVN_SECRET_KEY;

    @Value("${zetavn.access_token_expiration_time}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME;



    @PostMapping("/register")
    public ApiResponse<?> registerUser(@RequestBody SignUpRequest signUpRequest) {

        return authService.register(signUpRequest);
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody SignInRequest signInRequest) {
        // Authenticate the user
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(), signInRequest.getPassword());

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Username or Password!!");
        }
//        doAuthenticate(signInRequest);
        log.info("Try to login: {}", signInRequest.toString());
        // Fetch the user details

        return authService.login(signInRequest);
    }

    @GetMapping("/token/refresh")
    public ApiResponse<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        log.info("AuthorizationHeader: ", authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(ZETAVN_SECRET_KEY.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                UserEntity user = userRepository.findUserEntityByEmail(username);

                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withClaim("status", user.getStatus().toString())
                        .withIssuedAt(new Date(System.currentTimeMillis()))
                        .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();

                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                JwtResponse jwtResponse = new JwtResponse(tokens.get("access_token"), tokens.get("refresh_token"));
                return ApiResponse.success(HttpStatus.OK, "Refresh Token Success", jwtResponse);
            } catch (Exception e) {
                response.setHeader("error", e.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Refresh token is missing");
    }


}
