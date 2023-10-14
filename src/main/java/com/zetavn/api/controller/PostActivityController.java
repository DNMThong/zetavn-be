package com.zetavn.api.controller;

import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.impl.PostActivityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0/activity")
public class PostActivityController {
    @Autowired
    private PostActivityServiceImpl postActivityService;

    @GetMapping("")
    public ApiResponse<?> getAll() {
        return ApiResponse.success(HttpStatus.OK, "Get all success", postActivityService.getAll());
    }
}
