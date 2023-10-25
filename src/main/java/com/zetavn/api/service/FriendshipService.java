package com.zetavn.api.service;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FriendRequestResponse;
import com.zetavn.api.payload.response.FriendshipResponse;
import com.zetavn.api.payload.response.Paginate;

import java.util.List;

public interface FriendshipService {
    ApiResponse<FriendshipResponse> sendRequest(FriendshipRequest friendshipRequest);
    ApiResponse<Paginate<List<FriendRequestResponse>>> getReceiverFriendRequests(String receiverUserId,  Integer pageNumber, Integer pageSize);
    ApiResponse<FriendshipResponse> accept(Long friendshipId);
    ApiResponse<FriendshipResponse> rejected(Long friendshipId);
    ApiResponse<Paginate<List<FriendRequestResponse>>> getFriendsByUserId(String userId,  Integer pageNumber, Integer pageSize);

    ApiResponse<Paginate<List<FriendRequestResponse>>> getFriendSuggestions(String userId,  Integer pageNumber, Integer pageSize);

    ApiResponse<?> getFriendsByKeyword(String userId, String kw, Integer pageNumber, Integer pageSize);

    ApiResponse<?> getStrangersByKeyword(String userId, String kw, Integer pageNumber, Integer pageSize);
}
