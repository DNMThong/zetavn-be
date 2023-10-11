package com.zetavn.api.service;

import com.zetavn.api.payload.request.SignInRequest;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.SignInResponse;
import com.zetavn.api.payload.response.UserResponse;

public interface AuthService {

    ApiResponse<?> register(SignUpRequest signUpRequest);

    ApiResponse<?> login(SignInRequest signInRequest);

}