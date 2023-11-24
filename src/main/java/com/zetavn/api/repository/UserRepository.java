package com.zetavn.api.repository;

import com.zetavn.api.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findUserEntityByEmail(String email);
    UserEntity findUserEntityByUsername(String username);

    UserEntity findUserEntitiesByToken(String token);

    @Query("SELECT o FROM UserEntity o WHERE o.username LIKE ?1 OR o.email LIKE ?2")
    UserEntity findUserEntityByUsernameAndEmail(String username, String email);
    @Query("SELECT DISTINCT (o) FROM UserEntity o WHERE (LOWER(CONCAT(o.firstName, ' ', o.lastName)) LIKE CONCAT('%', LOWER(:keyword), '%')) AND o.userId <> :sourceId")
    Page<UserEntity> findUserEntityByKeyword(@Param("sourceId") String sourceId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT (o) FROM UserEntity o WHERE o.userId IN :listId AND (LOWER(CONCAT(o.firstName, ' ', o.lastName)) LIKE CONCAT('%', LOWER(:keyword), '%')) AND o.userId <> :sourceId")
    Page<UserEntity> findUserEntitiesByFriendList(@Param("sourceId") String sourceId, @Param("listId") List<String> listId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT (o) FROM UserEntity o WHERE o.userId NOT IN :listId AND (LOWER(CONCAT(o.firstName, ' ', o.lastName)) LIKE CONCAT('%', LOWER(:keyword), '%')) AND o.userId <> :sourceId")
    Page<UserEntity> findStrangersByKeyword(@Param("sourceId") String sourceId, @Param("listId") List<String> listId, @Param("keyword") String keyword, Pageable pageable);


    @Query("select u from UserEntity u " +
            "where u.userId in (select f.receiverUserEntity.userId from FriendshipEntity f where f.senderUserEntity.userId = :userId and f.status = 'ACCEPTED') " +
            "or u.userId in (select f.senderUserEntity.userId from FriendshipEntity f where f.receiverUserEntity.userId = :userId and f.status = 'ACCEPTED')")
    List<UserEntity> findFriendsByUser(@Param("userId") String userId);

    @Query("select u from UserEntity u " +
            "where u.userId in (select m.recieverUser.userId from MessageEntity m where m.senderUser.userId = :userId) " +
            "or u.userId in (select m.senderUser.userId from MessageEntity m where m.recieverUser.userId = :userId )")
    List<UserEntity> findUserContracts(@Param("userId") String userId);


}
