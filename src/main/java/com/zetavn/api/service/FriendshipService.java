package com.zetavn.api.service;

import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FriendshipResponse;
import com.zetavn.api.payload.response.OverallUserResponse;
import com.zetavn.api.payload.response.UserResponse;

import java.util.List;

public interface FriendshipService {
    ApiResponse<FriendshipResponse> sendRequest(FriendshipRequest friendshipRequest);
    ApiResponse<List<OverallUserResponse>> getReceiverFriendRequests(String receiverUserId);
    ApiResponse<FriendshipResponse> accept(Long friendshipId);
    ApiResponse<FriendshipResponse> rejected(Long friendshipId);
    ApiResponse<List<OverallUserResponse>> getFriendsByUserId(String userId);

    ApiResponse<List<OverallUserResponse>> getFriendSuggestions(String userId);
}
