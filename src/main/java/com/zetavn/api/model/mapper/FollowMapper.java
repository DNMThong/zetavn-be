package com.zetavn.api.model.mapper;

import com.zetavn.api.model.entity.FollowEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.FollowResponse;
import com.zetavn.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FollowMapper {
    private UserRepository userRepository;
    @Autowired
    FollowMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public FollowResponse entityToFollowResponse(FollowEntity followEntity) {
        FollowResponse followResponse = new FollowResponse();
        followResponse.setFollowId(followEntity.getFollowsId());
        followResponse.setFollowerUserId(followEntity.getFollowerUserEntity().getUserId());
        followResponse.setFollowingUserId(followEntity.getFollowingUserEntity().getUserId());
        followResponse.setPriority(followEntity.getPriority());
        return followResponse;
    }
    public FollowEntity commentRequestToEntity(FollowRequest followRequest) {
        FollowEntity followEntity = new FollowEntity();
        Optional<UserEntity> userFollower = userRepository.findById(followRequest.getFollowerId());
        followEntity.setFollowerUserEntity(userFollower.get());
        Optional<UserEntity> userFollowing = userRepository.findById(followRequest.getFollowingId());
        followEntity.setFollowingUserEntity(userFollowing.get());
        followEntity.setPriority(followRequest.getPriority());
        return followEntity;
    }
}
