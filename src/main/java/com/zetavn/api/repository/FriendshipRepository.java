package com.zetavn.api.repository;

import com.zetavn.api.enums.FriendStatusEnum;
import com.zetavn.api.model.entity.FriendshipEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.response.FriendshipResponse;
import com.zetavn.api.payload.response.UserResponse;
//import com.zetavn.api.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    @Query("SELECT f from  FriendshipEntity f " +
            "WHERE f.senderUserEntity.userId = :senderId " +
            "AND   f.receiverUserEntity.userId = :receiverId")
    FriendshipEntity getFriendShipById(String senderId, String receiverId);
    @Query("SELECT f.senderUserEntity FROM FriendshipEntity f " +
            "WHERE f.receiverUserEntity.userId = :userId AND f.status = 'ACCEPTED'")
    List<UserEntity> findFriendsSentToUser(@Param("userId") String userId);

    @Query("SELECT f FROM FriendshipEntity f WHERE f.senderUserEntity = :senderUser AND f.receiverUserEntity = :receiverUser")
    FriendshipEntity findFriendshipBySenderUserAndReceiverUser(UserEntity senderUser, UserEntity receiverUser);
    @Query("select f from FriendshipEntity f where f.senderUserEntity.userId = :senderUserEntity_userId " +
            "                                  and f.receiverUserEntity.userId = :receiverUserEntity_userId or f.senderUserEntity.userId = :receiverUserEntity_userId and f.receiverUserEntity.userId = :senderUserEntity_userId")
    FriendshipEntity getFriendshipByUserID(String senderUserEntity_userId, String receiverUserEntity_userId);
    @Query("SELECT f FROM FriendshipEntity f " +
            "WHERE f.senderUserEntity.userId = :id and f.status = 'ACCEPTED'" +
            "OR f.receiverUserEntity.userId = :id AND f.status = 'PENDING'" +
            "ORDER BY f.createdAt DESC")
    Page<FriendshipEntity> getFriendRequestState (String id, Pageable pageable);

    @Query("SELECT f.senderUserEntity FROM FriendshipEntity f " +
            "WHERE f.receiverUserEntity.userId = :userId AND f.status = 'ACCEPTED'")
    Page<UserEntity> findFriendsSentToUserPageable(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT f.receiverUserEntity FROM FriendshipEntity f " +
            "WHERE f.senderUserEntity.userId = :userId AND f.status = 'ACCEPTED'")
    Page<UserEntity> findFriendsReceivedByUser(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT f.receiverUserEntity FROM FriendshipEntity f " +
            "WHERE f.senderUserEntity.userId = :userId AND f.status = 'ACCEPTED'")
    List<UserEntity> findFriendsReceivedByUser(@Param("userId") String userId);


    @Query("SELECT u " +
            "FROM UserEntity u " +
            "WHERE u.userId NOT IN " +
            "(SELECT f.senderUserEntity FROM FriendshipEntity f " +
            "WHERE f.receiverUserEntity.userId = :userId) " +
            "AND u.userId NOT IN " +
            "(SELECT f.receiverUserEntity FROM FriendshipEntity f " +
            "WHERE f.senderUserEntity.userId = :userId) " +
            "AND u.userId <> :userId")
    Page<UserEntity> findSuggestionsForUser(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT f.senderUserEntity FROM FriendshipEntity f " +
            "WHERE f.receiverUserEntity.userId = :userId AND f.status = 'ACCEPTED' AND (LOWER(CONCAT(f.receiverUserEntity.firstName, ' ', f.receiverUserEntity.lastName)) LIKE CONCAT('%', LOWER(:kw), '%'))")
    Page<UserEntity> findFriendsSentByKeyword(@Param("userId") String userId, @Param("kw") String kw, Pageable pageable);

    @Query("SELECT f.receiverUserEntity FROM FriendshipEntity f " +
            "WHERE f.senderUserEntity.userId = :userId AND f.status = 'ACCEPTED' AND (LOWER(CONCAT(f.senderUserEntity.firstName, ' ', f.senderUserEntity.lastName)) LIKE CONCAT('%', LOWER(:kw), '%'))")
    Page<UserEntity> findFriendsReceivedByKeyword(@Param("userId") String userId, @Param("kw") String kw, Pageable pageable);

//    (f.senderUserEntity.firstName LIKE %:kw% OR f.senderUserEntity.lastName LIKE %:kw/)


    @Query("SELECT u " +
            "FROM UserEntity u " +
            "WHERE u.userId <> :userId " +
            "AND (u.userId NOT IN " +
            "(SELECT f.senderUserEntity.userId FROM FriendshipEntity f WHERE f.receiverUserEntity.userId = :userId) " +
            "AND u.userId NOT IN " +
            "(SELECT f.receiverUserEntity.userId FROM FriendshipEntity f WHERE f.senderUserEntity.userId = :userId)) " +
            "AND (u.lastName LIKE %:kw% OR u.firstName LIKE %:kw%)")
    Page<UserEntity> findStrangersByKeyword(@Param("userId") String userId, @Param("kw") String kw, Pageable pageable);

    @Query("SELECT COUNT(f) FROM FriendshipEntity f WHERE (f.senderUserEntity.userId = :userId OR f.receiverUserEntity.userId = :userId) AND f.status = 'ACCEPTED'")
    Long countFriends(@Param("userId") String id);

    @Query("SELECT f FROM FriendshipEntity f " +
            "WHERE (f.senderUserEntity.userId = :sourceId AND f.receiverUserEntity.userId = :targetId) " +
            "OR (f.senderUserEntity.userId = :targetId AND f.receiverUserEntity.userId = :sourceId)")
    FriendshipEntity checkFriendshipStatus(@Param("sourceId") String sourceId, @Param("targetId") String targetId);

}
