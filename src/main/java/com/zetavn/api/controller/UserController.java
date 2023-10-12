package com.zetavn.api.controller;

import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @GetMapping("/search")
    public ApiResponse<?> getUsersByKeyword(@RequestParam(name = "kw") String kw,
                                            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return userService.getAllUsersByKeyword(kw, pageNumber, pageSize);
    }
}
