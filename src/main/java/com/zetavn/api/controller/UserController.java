package com.zetavn.api.controller;

import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("")
    public ApiResponse<?> getAllUsers() {
        return ApiResponse.success(HttpStatus.OK, "sucess", userService.getAllUsers());
    }
}
