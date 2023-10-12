package com.zetavn.api.controller.user;

import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FollowResponse;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v0")
public class FollowController {
    private final FollowService followService;
    private final UserRepository userRepository;
    @Autowired
    FollowController(FollowService followService, UserRepository userRepository) {
        this.followService = followService;
        this.userRepository = userRepository;
    }

    @PostMapping("/follows")
    public ApiResponse<FollowResponse> createFollow(@RequestBody FollowRequest followRequest) {
        return followService.createFollow(followRequest);
    }


    @Transactional
    @DeleteMapping("/follows")
    public ApiResponse<?> unfollow(@RequestParam String followerUserId,
                                   @RequestParam String followingUserId) {
        Optional<UserEntity> followerUser = userRepository.findById(followerUserId);
        Optional<UserEntity> followingUser = userRepository.findById(followingUserId);
        if(followerUser.isEmpty() || followingUser.isEmpty()) {
            throw new NotFoundException("Not found user");
        }
        boolean unfollow = followService.deleteFollow(followerUserId, followingUserId);
        String message = followerUserId + " unfollow " + followingUserId;
        if (unfollow) {
            return ApiResponse.success(HttpStatus.OK, message, null);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Not found follows");
        }
    }

    @GetMapping("/following-users/{followerUserId}")//Lấy danh sách followerUserId(người dùng) đang theo dõi ai
    public ApiResponse<List<UserResponse>> getFollowingUsers(@PathVariable("followerUserId") String followerUserId) {
        return followService.getFollowingUsers(followerUserId);
    }

    @GetMapping("/users/{userId}/followers")//Lấy danh sách ai đang theo dõi UserId này
    public ApiResponse<List<UserResponse>> getFollowers(@PathVariable("userId") String userId) {
        return followService.getFollower(userId);
    }

    @PutMapping("/follows/updatePriority")
    public ApiResponse<FollowResponse> updatePriority(@RequestBody FollowRequest followRequest) {
        return followService.updatePriority(followRequest);
    }

}
