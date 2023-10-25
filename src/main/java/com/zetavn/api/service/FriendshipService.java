package com.zetavn.api.service;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.*;

import java.util.List;

public interface FriendshipService {
    ApiResponse<FriendshipResponse> sendRequest(FriendshipRequest friendshipRequest);
    ApiResponse<Paginate<List<FriendRequestResponse>>> getReceiverFriendRequests(String receiverUserId,  Integer pageNumber, Integer pageSize);
    ApiResponse<FriendshipResponse> accept(Long friendshipId);
    ApiResponse<FriendshipResponse> rejected(Long friendshipId);
    ApiResponse<Paginate<List<FriendRequestResponse>>> getFriendsByUserIdPaginate(String userId,  Integer pageNumber, Integer pageSize);
    ApiResponse<List<FriendRequestResponse>> getFriendsByUserId(String userId);
    ApiResponse<Paginate<List<FriendRequestResponse>>> getFriendSuggestions(String userId,  Integer pageNumber, Integer pageSize);

    ApiResponse<?> getFriendsByKeyword(String userId, String kw, Integer pageNumber, Integer pageSize);

    ApiResponse<?> getStrangersByKeyword(String userId, String kw, Integer pageNumber, Integer pageSize);
}
