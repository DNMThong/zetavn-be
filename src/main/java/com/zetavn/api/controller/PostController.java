package com.zetavn.api.controller;

import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.impl.PostServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/posts")
@Slf4j
public class PostController {
    @Autowired
    private PostServiceImpl postService;

    @GetMapping("/{postId}")
    public ApiResponse<?> getPostById(@PathVariable String postId) {
        return ApiResponse.success(HttpStatus.OK, "Get post success", postService.getPostById(postId));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<?> getAllPostByUserId(@PathVariable String userId) {
        return postService.getAllPostByUserId(userId);
    }

    @PostMapping("")
    public ApiResponse<?> createPost(@RequestBody PostRequest postRequest) {
        return postService.createPost(postRequest);
    }
}
