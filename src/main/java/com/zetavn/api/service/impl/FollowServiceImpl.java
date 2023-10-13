package com.zetavn.api.service.impl;

import com.zetavn.api.enums.FollowPriorityEnum;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.FollowEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.FollowMapper;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FollowResponse;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.repository.FollowRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowMapper followMapper;

    @Autowired
    FollowServiceImpl(FollowRepository followRepository, UserRepository userRepository,FollowMapper followMapper) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.followMapper = followMapper;
    }

    @Override
    public ApiResponse<FollowResponse> createFollow(FollowRequest followRequest) {
        Optional<FollowEntity> check = followRepository.findFollowEntityByFollowerUserEntityUserIdAndFollowingUserEntityUserId(followRequest.getFollowerUserId(), followRequest.getFollowingUserId());
        if (check.isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Friend request already exists", null);
        }
        FollowEntity followEntity = followMapper.commentRequestToEntity(followRequest);
        followEntity.setCreatedAt(LocalDateTime.now());
        followEntity.setPriority(FollowPriorityEnum.MEDIUM);
        FollowEntity saveFollow = followRepository.save(followEntity);
        return ApiResponse.success(HttpStatus.OK, "", followMapper.entityToFollowResponse(saveFollow));
    }

    @Override
    public ApiResponse<FollowResponse> updatePriority(Long followId, String priority) {
        Optional<FollowEntity> follow = followRepository.findById(followId);
        if (follow.isEmpty()) throw new NotFoundException("Follow not found");
        FollowEntity newPriority = follow.get();
        newPriority.setPriority(FollowPriorityEnum.valueOf(priority));
        followRepository.save(newPriority);
        return ApiResponse.success(HttpStatus.OK,"", followMapper.entityToFollowResponse(newPriority));
    }

    @Override
    public ApiResponse<Object> deleteFollow(String followerUserId, String followingUserId) {
        Optional<UserEntity> followerUser = userRepository.findById(followerUserId);
        Optional<UserEntity> followingUser = userRepository.findById(followingUserId);
        if(followerUser.isEmpty() || followingUser.isEmpty()) {
            throw new NotFoundException("Not found user");
        }
        followRepository.deleteByFollowerUserEntityUserIdAndFollowingUserEntityUserId(followerUserId, followingUserId);
        boolean isFollowing = followRepository.existsByFollowerIdAndFollowingId(followerUserId, followingUserId);
        String message = followerUserId + " unfollow " + followingUserId;

        if (isFollowing) {
            return ApiResponse.success(HttpStatus.OK, message, null);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Not found follows", null);
        }
    }

    @Override
    public ApiResponse<List<UserResponse>> getFollowingUsers(String followerUserId) {
        Optional<UserEntity> u = userRepository.findById(followerUserId);
        if(u.isEmpty()) {
            throw new NotFoundException("Not found user with userId: " + u);
        }
        List<UserEntity> user = followRepository.getFollowingUsers(followerUserId);
        List<UserResponse> userResponses = user.stream().map(UserMapper::userEntityToUserResponse).toList();
        return ApiResponse.success(HttpStatus.OK, "", userResponses);
    }

    @Override
    public ApiResponse<List<UserResponse>> getFollower(String userId) {
        Optional<UserEntity> u = userRepository.findById(userId);
        if(u.isEmpty()) {
            throw new NotFoundException("Not found user with userId: " + u);
        }
        List<UserEntity> user = followRepository.getFollowers(userId);
        List<UserResponse> userResponses = user.stream().map(UserMapper::userEntityToUserResponse).toList();
        return ApiResponse.success(HttpStatus.OK, "", userResponses);
    }

    @Override
    public void friendshipFollow(FollowRequest followRequest) {
        FollowEntity followEntity = followMapper.commentRequestToEntity(followRequest);
        followEntity.setCreatedAt(LocalDateTime.now());
        followEntity.setPriority(FollowPriorityEnum.MEDIUM);
        followRepository.save(followEntity);
    }
}
