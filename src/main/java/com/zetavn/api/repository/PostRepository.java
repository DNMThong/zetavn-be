package com.zetavn.api.repository;

import com.zetavn.api.enums.PostStatusEnum;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    Long countPostEntityByUserEntityUserId(String id);

    @Query("SELECT COUNT(pl) FROM PostLikeEntity pl " +
            "WHERE pl.userEntity.userId = :id")
    Long getTotalLikesByUserId(@Param("id") String id);

    @Query("SELECT o FROM PostEntity o WHERE DATE(o.createdAt) >=?1 AND DATE(o.createdAt) <=?2  ORDER BY o.createdAt DESC")
    Page<PostEntity> statisticCreatAtPosts(LocalDate startDay, LocalDate endDay, Pageable pageable);

    @Query("SELECT o FROM PostEntity o WHERE o.status=?1 AND o.isDeleted=false ORDER BY o.createdAt DESC")
    Page<PostEntity> getAllPostsForAdminByStatus(PostStatusEnum postStatusEnum, Pageable pageable);

    @Query("SELECT o FROM PostEntity o ORDER BY o.createdAt DESC")
    Page<PostEntity> findAllPosts(Pageable pageable);

    @Query("SELECT COUNT(o) FROM PostEntity o WHERE  DATE(o.createdAt) >=?1 AND DATE(o.createdAt) <=?2")
    Long countPostsInDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT  p  FROM PostEntity p LEFT JOIN p.commentEntityList c LEFT JOIN p.postLikeEntityList pl GROUP BY p ORDER BY (COUNT(c) * 1.2 + COUNT(pl)) DESC")
    Page<PostEntity> findTop10PostsByCommentAndLike(Pageable pageable);


    @Query("SELECT p.postEntity FROM PostMediaEntity p WHERE p.userEntity.userId = ?1 ORDER BY p.postEntity.createdAt DESC")
    Page<PostEntity> getPostsWithMediaByUserId(String userId, Pageable pageable);
}
