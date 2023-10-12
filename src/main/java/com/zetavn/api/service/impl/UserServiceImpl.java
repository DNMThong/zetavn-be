package com.zetavn.api.service.impl;

import com.zetavn.api.enums.UserStatusEnum;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;

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
    @Override
    public ApiResponse<?> getAllUsersByKeyword(String keyword, Integer pageNumber, Integer pageSize) {
        log.info("Try to find Users by keyword {} at page number {} and page size {}", keyword, pageNumber, pageSize);
        if (pageNumber < 0 || pageSize < 0) {
            log.error("Error Logging: pageNumber {} < 0 || pageSize {} < 0 with keyword {}", pageNumber, pageSize, keyword);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Page number and page size must be positive");
        } else {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<UserEntity> users = userRepository.findUserEntityByKeyword(keyword, pageable);
            if (pageNumber > users.getTotalPages()) {
                log.error("Error Logging: pageNumber: {} is out of total_page: {}", pageNumber, users.getNumber());
                throw new InvalidParameterException("pageNumber is out of total Page");
            }
            try {
                List<UserEntity> userEntities = users.getContent();
                List<UserResponse> userResponses = userEntities.stream().map(UserMapper::userEntityToUserResponse).toList();
                Paginate<List<UserResponse>> dataResponse = new Paginate<>();
                dataResponse.setData(userResponses);
                dataResponse.setPageNumber(users.getNumber());
                dataResponse.setPageSize(users.getSize());
                dataResponse.setTotalElements(users.getTotalElements());
                dataResponse.setTotalPages(users.getTotalPages());
                dataResponse.setLastPage(users.isLast());
                return ApiResponse.success(HttpStatus.OK, "Success", dataResponse);
            } catch (Exception e) {
                log.error("Error Logging: pageNumber: {}, pageSize: {}, keyword: {}, error_message: {}", pageNumber, pageSize, keyword, e.getMessage());
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
            }
        }

    }
}
