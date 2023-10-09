package com.zetavn.api.controller.user;

import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FollowResponse;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0")
public class FollowController {
    private FollowService followService;
    @Autowired
    FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/follows")
    public ApiResponse<FollowResponse> createFollow(@RequestBody FollowRequest followRequest) {
        FollowResponse followResponse = followService.createFollow(followRequest);
        return ApiResponse.success(HttpStatus.OK, "", followResponse);
    }


    @Transactional
    @DeleteMapping("/follows")
    public ApiResponse<?> unfollow(@RequestParam String followerUserId,
                                   @RequestParam String followingUserId) {
        boolean unfollow = followService.deleteFollow(followerUserId, followingUserId);
        String message = followerUserId + " unfollow " + followingUserId;
        if (unfollow) {
            return ApiResponse.success(HttpStatus.OK,message, null);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Not found follows");
        }
    }

    @GetMapping("/following-users/{followerUserId}")//Lấy danh sách followerUserId(người dùng) đang theo dõi ai
    public ApiResponse<List<UserResponse>> getFollowingUsers(@PathVariable("followerUserId") String followerUserId) {
        List<UserResponse> userResponseList = followService.getFollowingUsers(followerUserId);
        return ApiResponse.success(HttpStatus.OK, "Get Following User", userResponseList);
    }

    @GetMapping("/users/{userId}/followers")//Lấy danh sách ai đang theo dõi UserId này
    public ApiResponse<List<UserResponse>> getFollowers(@PathVariable("userId") String userId) {
        List<UserResponse> userResponseList = followService.getFollower(userId);
        return ApiResponse.success(HttpStatus.OK, "Get Following User", userResponseList);
    }

    @PutMapping("/follows/updatePriority")
    public ApiResponse<FollowResponse> updatePriority(@RequestBody FollowRequest followRequest) {
        FollowResponse followResponse = followService.updatePriority(followRequest);
        return ApiResponse.success(HttpStatus.OK, "Update Success", followResponse);
    }

}
