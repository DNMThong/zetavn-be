package com.zetavn.api.service.impl;

import com.zetavn.api.enums.FriendStatusEnum;
import com.zetavn.api.exception.DuplicateRecordException;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.FollowEntity;
import com.zetavn.api.model.entity.FriendshipEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.FriendshipMapper;
import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FriendshipResponse;
import com.zetavn.api.repository.FriendshipRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.FollowService;
import com.zetavn.api.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        Optional<FriendshipEntity> check = friendshipRepository.findFriendshipEntityBySenderUserEntityUserIdAndReceiverUserEntityUserId(friendshipRequest.getSenderUserId(), friendshipRequest.getReceiverUserId());
        if(check.isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Friend request already exists", null);
        }

        FriendshipEntity friendship = friendshipMapper.friendshipRequestToEntity(friendshipRequest);
        friendship.setStatus(FriendStatusEnum.PENDING);
        friendship.setCreatedAt(LocalDateTime.now());

        FollowRequest follow = new FollowRequest();
        follow.setFollowerUserId(friendship.getSenderUserEntity().getUserId());
        follow.setFollowingUserId(friendship.getReceiverUserEntity().getUserId());
        followService.friendshipFollow(follow);

        FriendshipEntity saveFriendship = friendshipRepository.save(friendship);
        return ApiResponse.success(HttpStatus.OK, "send request success!", friendshipMapper.entityToFriendshipResponse(saveFriendship));
    }

    @Override
    public ApiResponse<List<FriendshipResponse>> getReceiverFriendRequests(String receiverUserId) {
        Optional<UserEntity> user = userRepository.findById(receiverUserId);

        if(user.isEmpty()) throw new NotFoundException("Not found user with id: " + receiverUserId);

        List<FriendshipEntity> friendshipEntities = friendshipRepository.getReceivedFriendRequests(receiverUserId);
        return ApiResponse.success(HttpStatus.OK, "Get receiver friend request!", friendshipEntities.stream().map(friendshipMapper::entityToFriendshipResponse).toList());
    }

    @Override
    public ApiResponse<FriendshipResponse> accept(Long friendshipId) {
        FriendshipEntity friendship = friendshipRepository.findById(friendshipId).orElse(null);

        if (friendship == null || !friendship.getStatus().equals(FriendStatusEnum.PENDING)) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request", null);
        }

        friendship.setStatus(FriendStatusEnum.ACCEPTED);

        FollowRequest follow = new FollowRequest();
        follow.setFollowerUserId(friendship.getReceiverUserEntity().getUserId());
        follow.setFollowingUserId(friendship.getSenderUserEntity().getUserId());
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
}
