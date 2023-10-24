package com.zetavn.api.service;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FollowResponse;
import com.zetavn.api.payload.response.OverallUserResponse;
import com.zetavn.api.payload.response.Paginate;

import java.util.List;


public interface FollowService {
    ApiResponse<FollowResponse> createFollow(FollowRequest followRequest);
    ApiResponse<FollowResponse> updatePriority(Long followId, String priority);
    ApiResponse<Object> deleteFollow(String followerUserId, String followingUserId);
    ApiResponse<Paginate<List<OverallUserResponse>>> getFollowingUsers(String followerId, Integer pageNumber, Integer pageSize);
    ApiResponse<Paginate<List<OverallUserResponse>>> getFollower(String userId, Integer pageNumber, Integer pageSize);
    void friendshipFollow(FollowRequest followRequest);


}
