package com.zetavn.api.model.mapper;

import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.FriendshipEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.FriendRequestResponse;
import com.zetavn.api.payload.response.FriendshipResponse;
import com.zetavn.api.payload.response.OverallUserResponse;
import com.zetavn.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class FriendshipMapper {
    private final UserRepository userRepository;

    @Autowired
    FriendshipMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FriendshipResponse entityToFriendshipResponse(FriendshipEntity friendshipEntity) {
        FriendshipResponse friendshipResponse = new FriendshipResponse();
        friendshipResponse.setId(friendshipEntity.getFriendshipId());

        OverallUserResponse userSender = OverallUserMapper.entityToOverallUser(friendshipEntity.getSenderUserEntity());
        friendshipResponse.setSenderUser(userSender);

        OverallUserResponse userReceiver = OverallUserMapper.entityToOverallUser(friendshipEntity.getReceiverUserEntity());
        friendshipResponse.setReceiverUser(userReceiver);

        friendshipResponse.setStatus(friendshipEntity.getStatus());
        friendshipResponse.setCreatedAt(friendshipEntity.getCreatedAt());
        return friendshipResponse;
    }

    public FriendRequestResponse entityToFriendRequest(UserEntity userEntity, LocalDateTime createAt) {
        FriendRequestResponse friendRequest = new FriendRequestResponse();
        friendRequest.setUser(OverallUserMapper.entityToOverallUser(userEntity));
        friendRequest.setCreatedAt(createAt);
        return friendRequest;
    }



    public FriendshipEntity friendshipRequestToEntity(FriendshipRequest friendshipRequest) {
        FriendshipEntity friendshipEntity = new FriendshipEntity();

        Optional<UserEntity> senderUser = userRepository.findById(friendshipRequest.getSenderId());
        if(senderUser.isEmpty()) throw new NotFoundException("Not found senderUser with ID: " + friendshipRequest.getSenderId());
        friendshipEntity.setSenderUserEntity(senderUser.get());

        Optional<UserEntity> receiverUser = userRepository.findById(friendshipRequest.getReceiverId());
        if(receiverUser.isEmpty()) throw new NotFoundException("Not found receiverUser with ID: " + friendshipRequest.getReceiverId());
        friendshipEntity.setReceiverUserEntity(receiverUser.get());

        return friendshipEntity;
    }

}

