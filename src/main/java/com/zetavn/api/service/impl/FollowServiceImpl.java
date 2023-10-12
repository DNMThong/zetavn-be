package com.zetavn.api.service.impl;

import com.zetavn.api.exception.DuplicateRecordException;
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
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    @Autowired
    FollowServiceImpl(FollowRepository followRepository, UserRepository userRepository,FollowMapper followMapper, UserMapper userMapper) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.followMapper = followMapper;
        this.userMapper = userMapper;
    }

    @Override
    public ApiResponse<FollowResponse> createFollow(FollowRequest followRequest) {
        FollowEntity followEntity = followMapper.commentRequestToEntity(followRequest);
        followEntity.setCreatedAt(LocalDateTime.now());
        FollowEntity saveFollow = followRepository.save(followEntity);
        if(saveFollow.getFollowsId() == followEntity.getFollowsId()) {
            throw new DuplicateRecordException("Followed is existing");
        }
        return ApiResponse.success(HttpStatus.OK, "", followMapper.entityToFollowResponse(saveFollow));
    }

    @Override
    public ApiResponse<FollowResponse> updatePriority(FollowRequest followRequest) {
        Optional<UserEntity> userFollower = userRepository.findById(followRequest.getFollowerUserId());
        Optional<UserEntity> userFollowing = userRepository.findById(followRequest.getFollowingUserId());
        if (userFollower.isEmpty() || userFollowing.isEmpty())
            throw new NotFoundException("userId not found!");

        Optional<FollowEntity> follow = followRepository.findByFollowerUserEntityAndFollowingUserEntity(userFollower.get(),userFollowing.get());
        if (follow.isEmpty())
            throw new NotFoundException("Follow not found");

        FollowEntity newPriority = follow.get();
        newPriority.setPriority(followRequest.getPriority());
        followRepository.save(newPriority);
        return ApiResponse.success(HttpStatus.OK,"", followMapper.entityToFollowResponse(newPriority));
    }

    @Override
    public boolean deleteFollow(String followerUserId, String followingUserId) {
        if(followerUserId.isEmpty() || followingUserId.isEmpty()) {
            throw new NotFoundException("FollowUserId not found");
        }
        followRepository.deleteByFollowerUserEntityUserIdAndFollowingUserEntityUserId(followerUserId, followingUserId);
        boolean isFollowing = followRepository.existsByFollowerIdAndFollowingId(followerUserId, followingUserId);
        return !isFollowing;
    }

    @Override
    public ApiResponse<List<UserResponse>> getFollowingUsers(String followerUserId) {
        Optional<UserEntity> u = userRepository.findById(followerUserId);
        if(u.isEmpty()) {
            throw new NotFoundException("Not found user with userId: " + u);
        }
        List<UserEntity> user = followRepository.getFollowingUsers(followerUserId);
        List<UserResponse> userResponses = user.stream().map(userMapper::userEntityToUserResponse).toList();
        return ApiResponse.success(HttpStatus.OK, "", userResponses);
    }

    @Override
    public ApiResponse<List<UserResponse>> getFollower(String userId) {
        Optional<UserEntity> u = userRepository.findById(userId);
        if(u.isEmpty()) {
            throw new NotFoundException("Not found user with userId: " + u);
        }
        List<UserEntity> user = followRepository.getFollowers(userId);
        List<UserResponse> userResponses = user.stream().map(userMapper::userEntityToUserResponse).toList();
        return ApiResponse.success(HttpStatus.OK, "", userResponses);
    }
}
