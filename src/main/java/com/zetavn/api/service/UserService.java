package com.zetavn.api.service;

import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.UserResponse;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface UserService {

    ApiResponse<?> create(SignUpRequest signUpRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    ApiResponse<UserResponse> getUserByEmail(String email);

    List<UserResponse> getAllUsers();

    ApiResponse<UserResponse> update();

    ApiResponse<UserResponse> remove(String userId);

    ApiResponse<UserResponse> getUserByEmailOrUsername(String username, String email);

    ApiResponse<?> getAllUsersByKeyword(String sourceId, String keyword, Integer pageNumber, Integer pageSize);

//    ApiResponse<?>
}
