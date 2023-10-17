package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, String> {
    @Query("SELECT o FROM PostEntity o WHERE o.userEntity.userId LIKE ?1")
    List<PostEntity> getAllPostByUserId(String userId);

    @Modifying
    @Transactional
    @Query("UPDATE PostEntity pe SET pe.isDeleted = :isDeleted WHERE pe.postId = :postId")
    void disablePost(@Param("postId") String postId, @Param("isDeleted") boolean isDeleted);

    @Query("SELECT p FROM PostEntity p WHERE p.userEntity.userId IN :listUserId ORDER BY p.createdAt DESC")
    Page<PostEntity> getAllPostByUserList(List<String> listUserId, Pageable pageable);
}
