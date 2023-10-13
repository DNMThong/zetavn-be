package com.zetavn.api.service;

import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FriendshipResponse;

import java.util.List;

public interface FriendshipService {
    ApiResponse<FriendshipResponse> sendRequest(FriendshipRequest friendshipRequest);
    ApiResponse<List<FriendshipResponse>> getReceiverFriendRequests(String receiverUserId);

    ApiResponse<FriendshipResponse> accept(Long friendshipId);
    ApiResponse<FriendshipResponse> rejected(Long friendshipId);
}
