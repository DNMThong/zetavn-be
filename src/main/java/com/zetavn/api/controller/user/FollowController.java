package com.zetavn.api.controller.user;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FollowResponse;
import com.zetavn.api.payload.response.OverallUserResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<Paginate<List<OverallUserResponse>>> getFollowingUsers(@PathVariable("followerId") String followerId,
                                                                    @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                                    @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return followService.getFollowingUsers(followerId, pageNumber ,pageSize);
    }

    @GetMapping("/users/{userId}/followers")//Lấy danh sách ai đang theo dõi UserId này
    public ApiResponse<Paginate<List<OverallUserResponse>>> getFollowers(@PathVariable("userId") String userId,
                                                                         @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                                         @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return followService.getFollower(userId, pageNumber ,pageSize);
    }

    @PutMapping("/follows/update-priority")
    public ApiResponse<FollowResponse> updatePriority(@RequestParam Long id,
                                                      @RequestParam String priority) {
        return followService.updatePriority(id, priority);

    }

}
