package com.zetavn.api.service.impl;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetavn.api.enums.RoleEnum;
import com.zetavn.api.enums.UserStatusEnum;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.SignInRequest;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.JwtResponse;
import com.zetavn.api.payload.response.SignInResponse;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.AuthService;
import com.zetavn.api.service.RefreshTokenService;
import com.zetavn.api.service.UserService;
import com.zetavn.api.utils.JwtHelper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.*;

import static com.zetavn.api.utils.UUID.generateUUID;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Service @Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<?> register(SignUpRequest signUpRequest) {

        log.info("Try to create User in database");
        if (userRepository.findUserEntityByEmail(signUpRequest.getEmail()) != null) {
            log.error("User exist in DB: {}", signUpRequest.getEmail());
            return ApiResponse.error(HttpStatus.CONFLICT, "Email have been taken");
        } else {

            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(generateUUID());
            userEntity.setUsername(userEntity.getUserId());
            userEntity.setEmail(signUpRequest.getEmail());
            userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setUpdatedAt(LocalDateTime.now());
            userEntity.setStatus(UserStatusEnum.ACTIVE);
            userEntity.setRole(RoleEnum.USER);
            userEntity.setLastName(signUpRequest.getLastName());
            userEntity.setFirstName(signUpRequest.getFirstName());
            UserEntity _user = userRepository.save(userEntity);
            log.info("Register user in database success: {}", _user.getEmail());
            return ApiResponse.success(HttpStatus.OK, "Register success", UserMapper.userEntityToUserResponse(_user));
        }

    }

    @Override
    public ApiResponse<?> login(SignInRequest signInRequest, HttpServletResponse res) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(), signInRequest.getPassword());

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Username or Password!!");
        }
        // Fetch the user
        UserEntity user = userRepository.findUserEntityByEmail(signInRequest.getUsername());
        if (user != null) {
            // Generate a JWT token, Refresh token
            Map<String, String> tokens = jwtHelper.generateTokens(user);

            // Store refresh token to db
            log.info("_TOKEN: {}", tokens.get("refresh_token"));
            refreshTokenService.create(tokens.get("refresh_token"), user.getUserId(), "asdasd");

            // Add refresh token to Coookie
            Cookie c = new Cookie("refresh_token2", tokens.get("refresh_token"));
            c.setMaxAge(900);
            c.setHttpOnly(true);
            res.addCookie(c);


            // Build the response containing JWT token and refresh token
            SignInResponse response = new SignInResponse();
            JwtResponse jwtResponse = new JwtResponse(tokens.get("access_token"), tokens.get("refresh_token"));
            UserResponse userResponse = UserMapper.userEntityToUserResponse(user);
            response.setUserInfo(userResponse);
            response.setTokens(jwtResponse);

            return ApiResponse.success(HttpStatus.OK, "Login success", response);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Your username does not exist!");
        }

    }

    @Override
    public ApiResponse<?> reLogin(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

}
