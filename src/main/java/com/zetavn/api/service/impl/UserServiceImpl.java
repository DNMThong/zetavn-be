package com.zetavn.api.service.impl;

import com.zetavn.api.enums.UserStatusEnum;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.SignInRequest;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.zetavn.api.utils.UUID.generateUUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<?> create(SignUpRequest signUpRequest) {
        log.info("Try to create User in database");
        if (existUserByUsername(signUpRequest.getEmail())) {
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
            userEntity.setLastName(signUpRequest.getLastName());
            userEntity.setFirstName(signUpRequest.getFirstName());
            UserEntity _user = userRepository.save(userEntity);
            log.info("Create user in database success: {}", _user.getEmail());
            return ApiResponse.success(HttpStatus.OK, "Register success", UserMapper.userEntityToUserResponse(_user));
        }
    }

    @Override
    public ApiResponse<UserResponse> getUserByEmail(String email) {
        return null;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserResponse> page = userRepository.findAll().stream().map(UserMapper::userEntityToUserResponse).toList();
        return page;
    }

    @Override
    public ApiResponse<UserResponse> update() {
        return null;
    }

    @Override
    public ApiResponse<UserResponse> remove(String userId) {
        return null;
    }

    @Override
    public ApiResponse<UserResponse> getUserByEmailOrUsername(String username, String email) {
        return null;
    }


    private boolean existUserByEmail(String email) {
        log.info("Trying to check user exist by email: {}", email);
        if (email == null || email.equals("")) {
            log.error("Error Logging: UserService - existUserByEmail - Missing email arg");
            throw new IllegalArgumentException("UserService - existUserByEmail - Missing email arg");
        } else {
            UserEntity user = userRepository.findUserEntityByEmail(email);
            log.info("Found email in database: {}", email);
            return user == null;
        }
    }

    private boolean existUserByUsername(String username) {
        log.info("Trying to check user exist by username: {}", username);
        if (username == null || username.equals("")) {
            log.error("Error Logging: UserService - existUserByUsername - Missing username arg");
            throw new IllegalArgumentException("UserService - existUserByUsername - Missing username arg");
        } else {
            UserEntity user = userRepository.findUserEntityByUsername(username);
            log.info("Found username in database: {}", username);
            return user == null;
        }
    }

}
