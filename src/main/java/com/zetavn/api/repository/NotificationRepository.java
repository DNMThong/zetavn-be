package com.zetavn.api.repository;

import com.zetavn.api.enums.PostNotificationEnum;
import com.zetavn.api.model.entity.PostNotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<PostNotificationEntity, Long> {
    @Query("SELECT n FROM PostNotificationEntity n WHERE n.receivingUser.userId = :userId ORDER BY n.createdAt DESC")
    Page<PostNotificationEntity> getListNotification(@Param("userId") String userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM PostNotificationEntity n WHERE n.relatedId = :id AND n.type = :type")
    void deletePostNotification(@Param("id") Long id,@Param("type") PostNotificationEnum type);
}
