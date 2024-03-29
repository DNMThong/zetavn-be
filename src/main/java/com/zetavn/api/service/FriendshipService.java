package com.zetavn.api.service;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.*;

import java.util.List;

public interface FriendshipService {
    ApiResponse<FriendshipResponse> sendRequest(FriendshipRequest friendshipRequest);
    ApiResponse<Paginate<List<FriendRequestResponse>>> friendRequestState (String id,  Integer pageNumber, Integer pageSize);
//    ApiResponse<FriendshipResponse> accept(Long friendshipId);
    ApiResponse<FriendshipResponse> accept(String senderId, String receiverId);
//    ApiResponse<FriendshipResponse> rejected(Long friendshipId);
    ApiResponse<FriendshipResponse> rejected(String senderId, String receiverId);

    ApiResponse<FriendshipResponse> unfriend(String userUnfriend, String userUnfriended);

    ApiResponse<Paginate<List<FriendRequestResponse>>> getFriendsByUserIdPaginate(String userId,  Integer pageNumber, Integer pageSize);
    ApiResponse<List<OverallUserResponse>> getFriendsByUserId(String userId);
    ApiResponse<Paginate<List<OverallUserResponse>>> getFriendSuggestions(String userId,  Integer pageNumber, Integer pageSize);

    ApiResponse<ShortFriendshipResponse> getFriendshipStatus(String sourceId, String targetId);
}
