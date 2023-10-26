package com.zetavn.api.service.impl;

import com.zetavn.api.enums.FriendStatusEnum;
import com.zetavn.api.exception.DuplicateRecordException;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.FriendshipEntity;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.FriendshipMapper;
import com.zetavn.api.model.mapper.OverallUserMapper;
import com.zetavn.api.model.mapper.UserSearchMapper;
import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.*;
import com.zetavn.api.repository.FriendshipRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.FollowService;
import com.zetavn.api.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final FriendshipMapper friendshipMapper;
    private final FollowService followService;
    private final UserRepository userRepository;

    @Autowired
    FriendshipServiceImpl(FriendshipRepository friendshipRepository,
                          FriendshipMapper friendshipMapper,
                          FollowService followService,
                          UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.friendshipMapper = friendshipMapper;
        this.followService = followService;
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse<FriendshipResponse> sendRequest(FriendshipRequest friendshipRequest) {
        FriendshipEntity existingFriendship = friendshipRepository.getFriendshipByUserID(friendshipRequest.getSenderId(), friendshipRequest.getReceiverId());
        if(existingFriendship != null && existingFriendship.getStatus() == FriendStatusEnum.ACCEPTED) throw new DuplicateRecordException("This relationship already exists");

        if (existingFriendship != null) {
            if (existingFriendship.getStatus() == FriendStatusEnum.PENDING) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Friend request already exists", null);
            } else if (existingFriendship.getStatus() == FriendStatusEnum.REJECTED) {
                existingFriendship.setStatus(FriendStatusEnum.PENDING);
                existingFriendship.setCreatedAt(LocalDateTime.now());

                FollowRequest follow = new FollowRequest();
                follow.setFollowerId(existingFriendship.getSenderUserEntity().getUserId());
                follow.setFollowingId(existingFriendship.getReceiverUserEntity().getUserId());
                followService.friendshipFollow(follow);

                FriendshipEntity saveFriendship = friendshipRepository.save(existingFriendship);
                return ApiResponse.success(HttpStatus.OK, "send request success!", friendshipMapper.entityToFriendshipResponse(saveFriendship));
            }
        }

        FriendshipEntity friendship = friendshipMapper.friendshipRequestToEntity(friendshipRequest);
        friendship.setStatus(FriendStatusEnum.PENDING);
        friendship.setCreatedAt(LocalDateTime.now());

        FollowRequest follow = new FollowRequest();
        follow.setFollowerId(friendship.getSenderUserEntity().getUserId());
        follow.setFollowingId(friendship.getReceiverUserEntity().getUserId());
        followService.friendshipFollow(follow);

        FriendshipEntity saveFriendship = friendshipRepository.save(friendship);
        return ApiResponse.success(HttpStatus.OK, "send request success!", friendshipMapper.entityToFriendshipResponse(saveFriendship));
    }

    @Override
    public ApiResponse<Paginate<List<FriendRequestResponse>>> getReceiverFriendRequests(String receiverUserId,  Integer pageNumber, Integer pageSize) {
        Optional<UserEntity> user = userRepository.findById(receiverUserId);
        if(user.isEmpty()) throw new NotFoundException("Not found user with id: " + receiverUserId);
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<UserEntity> userEntities = friendshipRepository.getReceivedFriendRequests(receiverUserId, pageable);
            List<UserEntity> uL = userEntities.getContent();
            List<FriendRequestResponse> friendRequestResponses = new ArrayList<>();
            for (UserEntity userEntity : uL) {
                FriendshipEntity friendshipEntity = friendshipRepository.findFriendshipBySenderUserAndReceiverUser(userEntity, user.get());
                if (friendshipEntity != null) {
                    FriendRequestResponse friendRequestResponse = new FriendRequestResponse();
                    friendRequestResponse.setUser(OverallUserMapper.entityToDto(userEntity));
                    friendRequestResponse.setCreatedAt(friendshipEntity.getCreatedAt());
                    friendRequestResponses.add(friendRequestResponse);
                }
            }
            Paginate<List<FriendRequestResponse>> dataResponse = new Paginate<>(
                    userEntities.getNumber(),
                    userEntities.getSize(),
                    userEntities.getTotalElements(),
                    userEntities.getTotalPages(),
                    userEntities.isLast(),
                    friendRequestResponses
            );
            return ApiResponse.success(HttpStatus.OK, "Get received friend requests!", dataResponse);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid param!");
        }
    }

//    @Override
//    public ApiResponse<FriendshipResponse> accept(Long friendshipId) {
//        FriendshipEntity friendship = friendshipRepository.findById(friendshipId).orElse(null);
//
//        if (friendship == null || !friendship.getStatus().equals(FriendStatusEnum.PENDING)) {
//            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request", null);
//        }
//
//        friendship.setStatus(FriendStatusEnum.ACCEPTED);
//
//        FollowRequest follow = new FollowRequest();
//        follow.setFollowerId(friendship.getReceiverUserEntity().getUserId());
//        follow.setFollowingId(friendship.getSenderUserEntity().getUserId());
//        followService.friendshipFollow(follow);
//
//        FriendshipEntity updatedFriendship = friendshipRepository.save(friendship);
//
//        return ApiResponse.success(HttpStatus.OK, "Accept success!", friendshipMapper.entityToFriendshipResponse(updatedFriendship));
//    }
    @Override
    public ApiResponse<FriendshipResponse> accept(String senderUserId, String receiverUserId) {
        FriendshipEntity friendship = friendshipRepository.getFriendShipById(senderUserId, receiverUserId);

        if (friendship == null || !friendship.getStatus().equals(FriendStatusEnum.PENDING)) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request", null);
        }

        friendship.setStatus(FriendStatusEnum.ACCEPTED);

        FollowRequest follow = new FollowRequest();
        follow.setFollowerId(friendship.getReceiverUserEntity().getUserId());
        follow.setFollowingId(friendship.getSenderUserEntity().getUserId());
        followService.friendshipFollow(follow);

        FriendshipEntity updatedFriendship = friendshipRepository.save(friendship);

        return ApiResponse.success(HttpStatus.OK, "Accept success!", friendshipMapper.entityToFriendshipResponse(updatedFriendship));
    }

    @Override
    public ApiResponse<FriendshipResponse> rejected(String senderUserId, String receiverUserId) {
        FriendshipEntity friendship = friendshipRepository.getFriendShipById(senderUserId, receiverUserId);

        if (friendship == null || !friendship.getStatus().equals(FriendStatusEnum.PENDING)) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request", null);
        }

        friendship.setStatus(FriendStatusEnum.REJECTED);
        followService.deleteFollow(friendship.getSenderUserEntity().getUserId(), friendship.getReceiverUserEntity().getUserId());

        FriendshipEntity updatedFriendship = friendshipRepository.save(friendship);

        return ApiResponse.success(HttpStatus.OK, "Reject success!", friendshipMapper.entityToFriendshipResponse(updatedFriendship));
    }
//    @Override
//    public ApiResponse<FriendshipResponse> rejected(Long friendshipId) {
//        FriendshipEntity friendship = friendshipRepository.findById(friendshipId).orElse(null);
//
//        if (friendship == null || !friendship.getStatus().equals(FriendStatusEnum.PENDING)) {
//            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request", null);
//        }
//
//        friendship.setStatus(FriendStatusEnum.REJECTED);
//        followService.deleteFollow(friendship.getSenderUserEntity().getUserId(), friendship.getReceiverUserEntity().getUserId());
//
//        FriendshipEntity updatedFriendship = friendshipRepository.save(friendship);
//
//        return ApiResponse.success(HttpStatus.OK, "Reject success!", friendshipMapper.entityToFriendshipResponse(updatedFriendship));
//    }
    @Override
    public ApiResponse<Paginate<List<FriendRequestResponse>>> getFriendsByUserIdPaginate(String userId, Integer pageNumber, Integer pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<UserEntity> friendsSentToUser = friendshipRepository.findFriendsSentToUserPageable(userId, pageable);
            Page<UserEntity> friendsReceivedByUser = friendshipRepository.findFriendsReceivedByUser(userId, pageable);
            List<UserEntity> allFriends = new ArrayList<>(friendsSentToUser.getContent());
            allFriends.addAll(friendsReceivedByUser.getContent());

            List<FriendRequestResponse> friendResponses = new ArrayList<>();
            int number = Math.max(friendsSentToUser.getNumber(), friendsReceivedByUser.getNumber());
            int size = Math.max(friendsSentToUser.getSize(), friendsReceivedByUser.getSize());
            long totalElements = friendsSentToUser.getTotalElements() + friendsReceivedByUser.getTotalElements();
            int totalPages = Math.max(friendsSentToUser.getTotalPages(), friendsReceivedByUser.getTotalPages());
            boolean isLast = friendsSentToUser.isLast() && friendsReceivedByUser.isLast();

            for (UserEntity friend : allFriends) {
                FriendRequestResponse friendResponse = new FriendRequestResponse();
                friendResponse.setUser(OverallUserMapper.entityToDto(friend));
                friendResponse.setCreatedAt(null);
                friendResponses.add(friendResponse);
            }

            Paginate<List<FriendRequestResponse>> dataResponse = new Paginate<>(
                    number,
                    size,
                    totalElements,
                    totalPages,
                    isLast,
                    friendResponses
            );
            return ApiResponse.success(HttpStatus.OK, "List of friends", dataResponse);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid param!");
        }
    }

    @Override
    public ApiResponse<Paginate<List<FriendRequestResponse>>> getFriendSuggestions(String userId, Integer pageNumber, Integer pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<UserEntity> friendSuggestions = friendshipRepository.findSuggestionsForUser(userId, pageable);
            List<UserEntity> users = friendSuggestions.getContent();
            List<FriendRequestResponse> suggestionResponses = new ArrayList<>();
            for (UserEntity suggestion : users) {
                // Ánh xạ từ UserEntity sang FriendRequestResponse
                FriendRequestResponse suggestionResponse = new FriendRequestResponse();
                suggestionResponse.setUser(OverallUserMapper.entityToDto(suggestion));
                suggestionResponse.setCreatedAt(null);
                suggestionResponses.add(suggestionResponse);
            }
            Paginate<List<FriendRequestResponse>> dataResponse = new Paginate<>(
                    friendSuggestions.getNumber(),
                    friendSuggestions.getSize(),
                    friendSuggestions.getTotalElements(),
                    friendSuggestions.getTotalPages(),
                    friendSuggestions.isLast(),
                    suggestionResponses
            );
            return ApiResponse.success(HttpStatus.OK , "List of friend suggestions", dataResponse);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid param!");
        }
    }


    @Override
    public ApiResponse<List<OverallUserResponse>> getFriendsByUserId(String userId) {
        List<UserEntity> friendsSentToUser = friendshipRepository.findFriendsSentToUser(userId);
        List<UserEntity> friendsReceivedByUser = friendshipRepository.findFriendsReceivedByUser(userId);

        List<UserEntity> allFriends = new ArrayList<>(friendsSentToUser);
        allFriends.addAll(friendsReceivedByUser);
        List<OverallUserResponse> friendResponses = new ArrayList<>();

        for (UserEntity friend : allFriends) {
            OverallUserResponse overallUserResponse = OverallUserMapper.entityToDto(friend);
            friendResponses.add(overallUserResponse);
        }
        return ApiResponse.success(HttpStatus.OK , "List of friends", friendResponses);
    }
//
//    @Override
//    public ApiResponse<List<FriendRequestResponse>> getFriendSuggestions(String userId) {
//        List<UserEntity> friendSuggestions = friendshipRepository.findSuggestionsForUser(userId);
//        List<FriendRequestResponse> suggestionResponses = new ArrayList<>();
//        for (UserEntity suggestion : friendSuggestions) {
//            // Ánh xạ từ UserEntity sang FriendRequestResponse
//            FriendRequestResponse suggestionResponse = new FriendRequestResponse();
//            suggestionResponse.setUser(OverallUserMapper.entityToOverallUser(suggestion));
//            suggestionResponse.setCreatedAt(null);
//            suggestionResponses.add(suggestionResponse);
//        }
//
//        return ApiResponse.success(HttpStatus.OK , "List of friend suggestions", suggestionResponses);
//    }
}
