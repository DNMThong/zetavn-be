package com.zetavn.api.service;

import com.zetavn.api.payload.request.SignInRequest;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.SignInResponse;
import com.zetavn.api.payload.response.UserInfoResponse;
import com.zetavn.api.payload.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.http.HttpResponse;

public interface AuthService {

    ApiResponse<?> register(SignUpRequest signUpRequest);

    ApiResponse<?> login(SignInRequest signInRequest, HttpServletResponse response) throws IOException;

    ApiResponse<?> reLogin(HttpServletRequest request, HttpServletResponse response) throws IOException;

    ApiResponse<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
    ApiResponse<?> logout(HttpServletRequest request, HttpServletResponse response) throws IOException;
    ApiResponse<?> forgotPassword(String email);

    ApiResponse<?> resetPassword(String token,String password);

    ApiResponse<?> sendEmailConfirmation(String userId);
    ApiResponse<?> confirmationEmail(String token);

    ApiResponse<SignInResponse> loginForAdmin(SignInRequest signInRequest, HttpServletResponse response) throws Exception;

    ApiResponse<?> createForAdmin(SignUpRequest signUpRequest);

}
