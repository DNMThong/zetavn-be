package com.zetavn.api.controller;

import com.zetavn.api.payload.request.UserInfoRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FriendRequestResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.service.FriendshipService;
import com.zetavn.api.service.PostService;
import com.zetavn.api.service.UserInfoService;
import com.zetavn.api.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v0/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("")
    public ApiResponse<?> getAllUsers() {
        return ApiResponse.success(HttpStatus.OK, "sucess", userService.getAllUsers());
    }
    @GetMapping("/search")
    public ApiResponse<?> getUsersByKeyword(
                                            @RequestParam(name = "id") String id,
                                            @RequestParam(name = "kw") String kw,
                                            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
                                            @RequestParam(name = "option", defaultValue = "all", required = false) String option) {
        switch (option) {
            case "friends": {
                return friendshipService.getFriendsByKeyword(id, kw, pageNumber, pageSize);
            }
            case "strange": {
                return friendshipService.getStrangersByKeyword(id, kw, pageNumber, pageSize);
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

    @GetMapping("/{userId}")
    public ApiResponse<?> getUserProfile(@PathVariable(name = "userId", required = true) Optional<String> userId) {
        return userInfoService.getUserInfoByUserId(userId.get());
    }

    @PutMapping("/{userId}/info")
    public ApiResponse<?> updateUserInfo(@PathVariable(name = "userId", required = true) Optional<String> userId, @RequestBody UserInfoRequest userInfoRequest) {
        if (userId.isEmpty())
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Missing userId");
        return userInfoService.update(userId.orElse(null), userInfoRequest);
    }

    @GetMapping("/{id}/friends")
    public ApiResponse<List<FriendRequestResponse>> getFriends(@PathVariable("id") String id) {
        return friendshipService.getFriendsByUserId(id);
    }
}
