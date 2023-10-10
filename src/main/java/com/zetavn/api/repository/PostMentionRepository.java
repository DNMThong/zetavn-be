package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostMentionEntity;
import com.zetavn.api.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostMentionRepository extends JpaRepository<PostMentionEntity, Long> {
    @Query("SELECT o.userEntity FROM PostMentionEntity o WHERE o.postEntity.postId LIKE ?1")
    List<UserEntity> getAllUserByPostId(String postId);
}
