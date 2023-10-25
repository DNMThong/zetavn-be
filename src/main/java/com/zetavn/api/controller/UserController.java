package com.zetavn.api.controller;

import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.PostService;
import com.zetavn.api.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

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

    @GetMapping("/{userId}/posts")
    public ApiResponse<?> getAllPostByUserId(@PathVariable String userId) {
        return postService.getAllPostByUserId(userId);
    }

    @GetMapping("/{id}/newsfeed")
    public ApiResponse<?> getPostById(@PathVariable(name = "id") String userId,
                                      @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                      @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return postService.getAllPostByUserFollow(userId, pageNumber, pageSize);
    }


}
