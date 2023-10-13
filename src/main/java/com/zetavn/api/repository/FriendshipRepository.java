package com.zetavn.api.repository;

import com.zetavn.api.enums.FriendStatusEnum;
import com.zetavn.api.model.entity.FriendshipEntity;
import com.zetavn.api.payload.response.FriendshipResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    @Query("select f from FriendshipEntity f where f.receiverUserEntity.userId = :receiverUserId and f.status = 'PENDING'")
    List<FriendshipEntity> getReceivedFriendRequests(String receiverUserId);

    Optional<FriendshipEntity> findFriendshipEntityBySenderUserEntityUserIdAndReceiverUserEntityUserId(String senderUserEntity_userId, String receiverUserEntity_userId);
}
