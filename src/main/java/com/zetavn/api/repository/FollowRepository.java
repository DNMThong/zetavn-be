package com.zetavn.api.repository;

import com.zetavn.api.enums.FollowPriorityEnum;
import com.zetavn.api.model.entity.FollowEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.response.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    void deleteByFollowerUserEntityUserIdAndFollowingUserEntityUserId(String followerUserEntity_userId, String followingUserEntity_userId);
    @Query("SELECT COUNT(f) > 0 FROM FollowEntity f WHERE f.followerUserEntity.userId = :followerUserId AND f.followerUserEntity.userId = :followingUserId")
    boolean existsByFollowerIdAndFollowingId(String followerUserId, String followingUserId);
    @Query("SELECT fe.followingUserEntity FROM FollowEntity fe WHERE fe.followerUserEntity.userId = :followerUserId")
    List<UserEntity> getFollowingUsers(String followerUserId);
    @Query("SELECT fe.followerUserEntity FROM FollowEntity fe WHERE fe.followingUserEntity.userId = :userId")
    List<UserEntity> getFollowers(String userId);

    Optional<FollowEntity> findByFollowerUserEntityAndFollowingUserEntity(UserEntity followerUserEntity, UserEntity followingUserEntity);

    Optional<FollowEntity> findFollowEntityByFollowerUserEntityUserIdAndFollowingUserEntityUserId(String followerUserEntity_userId, String followingUserEntity_userId);
}
