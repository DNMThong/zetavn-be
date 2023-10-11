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
    public ApiResponse<?> login(@RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        // Authenticate the user

//        doAuthenticate(signInRequest);
        log.info("Try to login: {}", signInRequest.toString());
        // Fetch the user details

        return authService.login(signInRequest, response);
    }

    @GetMapping("/token/refresh")
    public ApiResponse<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, IOException {
        String refresh_token = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie c: cookies) {
            if (c.getName().equals("refresh_token2")) {
                refresh_token = c.getValue();
            }
        }
        if (refresh_token != null && !refresh_token.equals("")) {
            try {
                DecodedJWT decodedJWT = jwtHelper.decodedJWTRef(refresh_token);
                if (decodedJWT.getExpiresAt().before(new Date())) {
                    log.info("Expires At: {}", decodedJWT.getExpiresAt().toInstant());
                    throw new TokenExpiredException("The token has expired", decodedJWT.getExpiresAt().toInstant());
                }
                String username = decodedJWT.getSubject();
                UserEntity user = userRepository.findUserEntityByEmail(username);

                String access_token = jwtHelper.generateToken(user);

                Map<String, String> tokens = new HashMap<>();

                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                JwtResponse jwtResponse = new JwtResponse(tokens.get("access_token"), tokens.get("refresh_token"));
                return ApiResponse.success(HttpStatus.OK, "Refresh token Success", jwtResponse);
            } catch (TokenExpiredException e) {
                response.setHeader("ERROR", e.getMessage());
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", "TokenExpired");
                error.put("message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            } catch (Exception e) {
                response.setHeader("ERROR", e.getMessage());
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", "TokenInvalid");
                error.put("message", "The Token is invalid");
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
        return ApiResponse.error(FORBIDDEN, "Refresh token is missing");
    }

    @GetMapping("/re-login")
    public ApiResponse<?> reLogin(HttpServletRequest request, HttpServletResponse response) {
        return authService.reLogin(request, response);
    }


}
