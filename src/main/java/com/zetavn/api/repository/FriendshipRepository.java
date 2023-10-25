package com.zetavn.api.repository;

import com.zetavn.api.enums.FriendStatusEnum;
import com.zetavn.api.model.entity.FriendshipEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.response.FriendshipResponse;
import com.zetavn.api.payload.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    @Query("SELECT f.senderUserEntity FROM FriendshipEntity f " +
            "WHERE f.receiverUserEntity.userId = :userId AND f.status = 'ACCEPTED'")
    List<UserEntity> findFriendsSentToUser(@Param("userId") String userId);
    @Query("select f.senderUserEntity from FriendshipEntity f where f.receiverUserEntity.userId = :receiverUserId and f.status = 'PENDING'" +
            "ORDER BY f.createdAt DESC")
    Page<UserEntity> getReceivedFriendRequests(String receiverUserId, Pageable pageable);
    @Query("SELECT f FROM FriendshipEntity f WHERE f.senderUserEntity = :senderUser AND f.receiverUserEntity = :receiverUser")
    FriendshipEntity findFriendshipBySenderUserAndReceiverUser(UserEntity senderUser, UserEntity receiverUser);

    @Query("select f from FriendshipEntity f where f.senderUserEntity.userId = :senderUserEntity_userId and f.receiverUserEntity.userId = :receiverUserEntity_userId")
    FriendshipEntity getFriendshipByUserID(String senderUserEntity_userId, String receiverUserEntity_userId);

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
            "WHERE f.receiverUserEntity.userId = :userId AND f.status = 'ACCEPTED' AND (f.receiverUserEntity.firstName LIKE %:kw% OR f.receiverUserEntity.lastName LIKE %:kw%)")
    Page<UserEntity> findFriendsSentByKeyword(@Param("userId") String userId, @Param("kw") String kw, Pageable pageable);

    @Query("SELECT f.receiverUserEntity FROM FriendshipEntity f " +
            "WHERE f.senderUserEntity.userId = :userId AND f.status = 'ACCEPTED' AND (f.senderUserEntity.firstName LIKE %:kw% OR f.senderUserEntity.lastName LIKE %:kw%)")
    Page<UserEntity> findFriendsReceivedByKeyword(@Param("userId") String userId, @Param("kw") String kw, Pageable pageable);


    @Query("SELECT u " +
            "FROM UserEntity u " +
            "WHERE u.userId <> :userId " +
            "AND (u.userId NOT IN " +
            "(SELECT f.senderUserEntity.userId FROM FriendshipEntity f WHERE f.receiverUserEntity.userId = :userId) " +
            "AND u.userId NOT IN " +
            "(SELECT f.receiverUserEntity.userId FROM FriendshipEntity f WHERE f.senderUserEntity.userId = :userId)) " +
            "AND (u.lastName LIKE %:kw% OR u.firstName LIKE %:kw%)")
    Page<UserEntity> findStrangersByKeyword(@Param("userId") String userId, @Param("kw") String kw, Pageable pageable);

}
