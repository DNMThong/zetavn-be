package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, String> {
    @Query("SELECT o FROM PostEntity o WHERE o.userEntity.userId LIKE ?1")
    List<PostEntity> getAllPostByUserId(String userId);
}
