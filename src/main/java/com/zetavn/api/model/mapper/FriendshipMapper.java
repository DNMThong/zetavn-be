package com.zetavn.api.model.mapper;

import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.FriendshipEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.FriendshipResponse;
import com.zetavn.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        friendshipResponse.setSenderUserId(friendshipEntity.getSenderUserEntity().getUserId());
        friendshipResponse.setReceiverUserId(friendshipEntity.getReceiverUserEntity().getUserId());
        friendshipResponse.setStatus(friendshipEntity.getStatus());
        return friendshipResponse;
    }

    public FriendshipEntity friendshipRequestToEntity(FriendshipRequest friendshipRequest) {
        FriendshipEntity friendshipEntity = new FriendshipEntity();

        Optional<UserEntity> senderUser = userRepository.findById(friendshipRequest.getSenderUserId());
        if(senderUser.isEmpty()) throw new NotFoundException("Not found senderUser with ID: " + friendshipRequest.getSenderUserId());
        friendshipEntity.setSenderUserEntity(senderUser.get());

        Optional<UserEntity> receiverUser = userRepository.findById(friendshipRequest.getReceiverUserId());
        if(receiverUser.isEmpty()) throw new NotFoundException("Not found receiverUser with ID: " + friendshipRequest.getReceiverUserId());
        friendshipEntity.setReceiverUserEntity(receiverUser.get());

        friendshipEntity.setStatus(friendshipRequest.getStatus());
        return friendshipEntity;

    }



}

