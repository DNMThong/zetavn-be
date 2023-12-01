package com.zetavn.api.controller;

import com.zetavn.api.payload.request.UploadImageBase64Response;
import com.zetavn.api.payload.request.UserInfoRequest;
import com.zetavn.api.payload.response.*;
import com.zetavn.api.service.FriendshipService;
import com.zetavn.api.service.PostService;
import com.zetavn.api.service.UserInfoService;
import com.zetavn.api.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v0/users")
public class UserController {

    UserService userService;

    PostService postService;

    private UserInfoService userInfoService;

    private FriendshipService friendshipService;

    @Autowired
    public UserController(UserService userService,PostService postService,UserInfoService userInfoService,FriendshipService friendshipService) {
        this.userService = userService;
        this.postService = postService;
        this.userInfoService = userInfoService;
        this.friendshipService = friendshipService;
    }

    @GetMapping("")
    public ApiResponse<?> getAllUsers() {

        return ApiResponse.success(HttpStatus.OK, "sucess", userService.getAllUsers());
    }
    @GetMapping("/search")
    public ApiResponse<?> getUsersByKeyword(
                                            @RequestParam(name = "kw") String kw,
                                            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
                                            @RequestParam(name = "option", defaultValue = "all", required = false) String option) {
        switch (option) {
            case "friends": {
                return userService.getAllFriendsByKeyword(kw, pageNumber, pageSize);
            }
            case "strangers": {
                return userService.getStrangersByKeyword(kw, pageNumber, pageSize);
            }
            case "all": {
                return userService.getAllUsersByKeyword(kw, pageNumber, pageSize);
            }
            default: {
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid option: Option(friends, strange, ?all)");
            }
        }
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

    @GetMapping("/{username}")
    public ApiResponse<?> getUserProfile(@PathVariable(name = "username", required = true) Optional<String> userId) {
        return userInfoService.getUserInfoByUsername(userId.get());
    }

    @PutMapping("/{userId}/info")
    public ApiResponse<?> updateUserInfo(@PathVariable(name = "userId", required = true) Optional<String> userId, @RequestBody UserInfoRequest userInfoRequest) {
        if (userId.isEmpty())
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Missing userId");
        return userInfoService.update(userId.orElse(null), userInfoRequest);
    }

    @GetMapping("/{id}/friends")
    public ApiResponse<List<OverallUserResponse>> getFriends(@PathVariable("id") String id) {
        return friendshipService.getFriendsByUserId(id);
    }

    @PutMapping("/{type}")
    public ApiResponse<UserResponse> updateAvatar(@RequestBody UploadImageBase64Response imageBase64, @PathVariable String type) {
        if (imageBase64.getImages().length == 0) {
            return ApiResponse.error(HttpStatus.NO_CONTENT, "Missing image");
        } else {
            switch (type) {
                case "avatar": {
                    return userService.updateAvatar(imageBase64.getImages()[0]);
                }
                case "poster": {
                    return userService.updatePoster(imageBase64.getImages()[0]);
                }
                default:
                    return ApiResponse.error(HttpStatus.NOT_ACCEPTABLE, "Type not acceptable");
            }
        }
    }

    @GetMapping("/contacts")
    public ApiResponse<List<UserContactResponse>> getContacts() {
        return userService.getUserContacts();
    }

}
