package com.zetavn.api.service;

import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {

    ApiResponse<?> create(SignUpRequest signUpRequest);

    ApiResponse<UserResponse> getUserByEmail(String email);

    List<UserResponse> getAllUsers();

    ApiResponse<UserResponse> update();

    ApiResponse<UserResponse> remove(String userId);

    ApiResponse<UserResponse> getUserByEmailOrUsername(String username, String email);

    ApiResponse<?> getAllUsersByKeyword(String keyword, Integer pageNumber, Integer pageSize);
}
