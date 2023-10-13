package com.zetavn.api.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetavn.api.exception.CustomExceptionHandler;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.request.SignInRequest;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.JwtResponse;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.AuthService;
import com.zetavn.api.utils.JwtHelper;
import jakarta.servlet.http.Cookie;
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
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
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

    @Autowired
    private JwtHelper jwtHelper;



    @PostMapping("/register")
    public ApiResponse<?> registerUser(@RequestBody SignUpRequest signUpRequest) {

        return authService.register(signUpRequest);
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody SignInRequest signInRequest, HttpServletResponse response) throws IOException {
        // Authenticate the user

//        doAuthenticate(signInRequest);
        log.info("Try to login: {}", signInRequest.toString());
        // Fetch the user details

        return authService.login(signInRequest, response);
    }

    @GetMapping("/token/refresh")
    public ApiResponse<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, IOException {
        return authService.refreshToken(request, response);
    }

    @GetMapping("/re-login")
    public ApiResponse<?> reLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return authService.reLogin(request, response);
    }


}
