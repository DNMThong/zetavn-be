package com.zetavn.api.service;

import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.response.FollowResponse;
import com.zetavn.api.payload.response.UserResponse;

import java.util.List;


public interface FollowService {
    FollowResponse createFollow(FollowRequest followRequest);
    FollowResponse updatePriority(FollowRequest followRequest);
    boolean deleteFollow(String followerUserId, String followingUserId);
    List<UserResponse> getFollowingUsers(String followerUserId);
    List<UserResponse> getFollower(String userId);

}
