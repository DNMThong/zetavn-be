package com.zetavn.api.service;

import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FollowResponse;
import com.zetavn.api.payload.response.UserResponse;

import java.util.List;


public interface FollowService {
    ApiResponse<FollowResponse> createFollow(FollowRequest followRequest);
    ApiResponse<FollowResponse> updatePriority(FollowRequest followRequest);
    boolean deleteFollow(String followerUserId, String followingUserId);
    ApiResponse<List<UserResponse>> getFollowingUsers(String followerUserId);
    ApiResponse<List<UserResponse>> getFollower(String userId);

}
