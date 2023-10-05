package com.zetavn.api.service.impl;

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
import com.zetavn.api.service.UserService;
import com.zetavn.api.utils.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service @Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

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
            userEntity.setUserId(shortUUID());
            userEntity.setUsername(userEntity.getUserId());
            userEntity.setEmail(signUpRequest.getEmail());
            userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setUpdatedAt(LocalDateTime.now());
            userEntity.setStatus(UserStatusEnum.ACTIVE);
            userEntity.setLastName(signUpRequest.getLastName());
            userEntity.setFirstName(signUpRequest.getFirstName());
            UserEntity _user = userRepository.save(userEntity);
            log.info("Register user in database success: {}", _user.getEmail());
            return ApiResponse.success(HttpStatus.OK, "Register success", UserMapper.userEntityToUserResponse(_user));
        }

    }

    @Override
    public ApiResponse<?> login(SignInRequest signInRequest) {
        log.info("Fetched User - " + signInRequest.toString());
//        doAuthenticate(signInRequest);


        // Fetch the user
        UserEntity user = userRepository.findUserEntityByEmail(signInRequest.getUsername());
        log.info("Fetched User - " + 123);
        // Generate a JWT token, Refresh token
        Map<String, String> tokens = jwtHelper.generateTokens(user);

        // Build the response containing JWT token and refresh token
        SignInResponse response = new SignInResponse();
        JwtResponse jwtResponse = new JwtResponse(tokens.get("access_token"), tokens.get("refresh_token"));
        UserResponse userResponse = UserMapper.userEntityToUserResponse(user);
        response.setUserInfo(userResponse);
        response.setTokens(jwtResponse);

        return ApiResponse.success(HttpStatus.OK, "Login success", response);
    }

    public static String shortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        log.info("Generating short UUID: {}", l);
        return Long.toString(l, Character.MAX_RADIX);
    }

    // Helper method for user authentication
//    private void doAuthenticate(SignInRequest request) {
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                request.getUsername(), request.getPassword());
//
//        try {
//            authenticationManager.authenticate(authenticationToken);
//        } catch (BadCredentialsException e) {
//            throw new BadCredentialsException("Invalid Username or Password!!");
//        }
//    }


}
