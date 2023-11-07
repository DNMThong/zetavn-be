package com.zetavn.api.controller.user;

import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FollowResponse;
import com.zetavn.api.payload.response.OverallUserResponse;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;
import java.util.List;

@RestController
@RequestMapping("/api/v0")
public class FollowController {
    private final FollowService followService;
    @Autowired
    FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/follows")
    public ApiResponse<FollowResponse> createFollow(@RequestBody FollowRequest followRequest) {
        return followService.createFollow(followRequest);
    }


    @Transactional
    @DeleteMapping("/follows")
    public ApiResponse<?> unfollow(@RequestParam String followerId,
                                   @RequestParam String followingId) {
        return followService.deleteFollow(followerId, followingId);
    }

    @GetMapping("/following-users/{followerId}")//Lấy danh sách followerUserId(người dùng) đang theo dõi ai
    public ApiResponse<List<OverallUserResponse>> getFollowingUsers(@PathVariable("followerId") String followerId) {
        return followService.getFollowingUsers(followerId);
    }

    @GetMapping("/users/{userId}/followers")//Lấy danh sách ai đang theo dõi UserId này
    public ApiResponse<List<OverallUserResponse>> getFollowers(@PathVariable("userId") String userId) {
        return followService.getFollower(userId);
    }

    @PutMapping("/follows/update-priority")
    public ApiResponse<FollowResponse> updatePriority(@RequestParam Long id, @RequestParam String priority) {
        return followService.updatePriority(id, priority);

    }

    @GetMapping("/follows/check-follow")
    public ApiResponse<FollowResponse> checkFollow(@RequestParam("sourceId") String sourceId, @RequestParam("targetId") String targetId) {
        return followService.getFollowStatus(sourceId, targetId);
    }


}
