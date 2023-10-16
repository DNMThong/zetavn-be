package com.zetavn.api.service.impl;

import com.zetavn.api.enums.FriendStatusEnum;
import com.zetavn.api.exception.DuplicateRecordException;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.FollowEntity;
import com.zetavn.api.model.entity.FriendshipEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.FriendshipMapper;
import com.zetavn.api.model.mapper.OverallUserMapper;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FriendshipResponse;
import com.zetavn.api.payload.response.OverallUserResponse;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.repository.FriendshipRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.FollowService;
import com.zetavn.api.service.FriendshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FriendshipServiceImpl implements FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final FriendshipMapper friendshipMapper;
    private final FollowService followService;
    private final UserRepository userRepository;

    @Autowired
    FriendshipServiceImpl(FriendshipRepository friendshipRepository,
                          FriendshipMapper friendshipMapper,
                          FollowService followService,
                          UserRepository userRepository
                          ) {
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

        // Tạo mối quan hệ bạn bè mới
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
    public ApiResponse<List<OverallUserResponse>> getReceiverFriendRequests(String receiverUserId) {
        Optional<UserEntity> user = userRepository.findById(receiverUserId);

        if(user.isEmpty()) throw new NotFoundException("Not found user with id: " + receiverUserId);

        List<UserEntity> friendshipEntities = friendshipRepository.getReceivedFriendRequests(receiverUserId);
        return ApiResponse.success(HttpStatus.OK, "Get receiver friend request!", friendshipEntities.stream().map(OverallUserMapper::entityToDto).toList());
    }

    @Override
    public ApiResponse<FriendshipResponse> accept(Long friendshipId) {
        FriendshipEntity friendship = friendshipRepository.findById(friendshipId).orElse(null);

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
    public ApiResponse<FriendshipResponse> rejected(Long friendshipId) {
        FriendshipEntity friendship = friendshipRepository.findById(friendshipId).orElse(null);

        if (friendship == null || !friendship.getStatus().equals(FriendStatusEnum.PENDING)) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request", null);
        }

        friendship.setStatus(FriendStatusEnum.REJECTED);
        followService.deleteFollow(friendship.getSenderUserEntity().getUserId(), friendship.getReceiverUserEntity().getUserId());

        FriendshipEntity updatedFriendship = friendshipRepository.save(friendship);

        return ApiResponse.success(HttpStatus.OK, "Reject success!", friendshipMapper.entityToFriendshipResponse(updatedFriendship));
    }

    @Override
    public ApiResponse<List<OverallUserResponse>> getFriendsByUserId(String userId) {
        List<UserEntity> friendsSentToUser = friendshipRepository.findFriendsSentToUser(userId);
        List<UserEntity> friendsReceivedByUser = friendshipRepository.findFriendsReceivedByUser(userId);

        List<UserEntity> allFriends = new ArrayList<>(friendsSentToUser);
        allFriends.addAll(friendsReceivedByUser);

        return ApiResponse.success(HttpStatus.OK , "list friends", allFriends.stream().map(OverallUserMapper::entityToDto).toList());
    }

    @Override
    public ApiResponse<List<OverallUserResponse>> getFriendSuggestions(String userId) {
        List<UserEntity> friendSuggestions = friendshipRepository.findSuggestionsForUser(userId);
        return ApiResponse.success(HttpStatus.OK , "list friend suggestions", friendSuggestions.stream().map(OverallUserMapper::entityToDto).toList());
    }
}
