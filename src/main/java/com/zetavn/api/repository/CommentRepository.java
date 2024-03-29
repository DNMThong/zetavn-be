package com.zetavn.api.repository;

import com.zetavn.api.model.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("SELECT c FROM CommentEntity c WHERE c.commentId = :commentParentId")
    CommentEntity getCommentEntityByCommentParentId(Long commentParentId);
    @Query("SELECT o FROM CommentEntity o WHERE o.postEntity.postId = ?1 AND o.commentEntityParent.commentId IS NULL ORDER BY o.createdAt DESC")
    Page<CommentEntity> findCommentsByPostId(String postId, Pageable pageable);

    @Query("select o from CommentEntity o where o.postEntity.postId = ?1 AND o.commentEntityParent.commentId= ?2 ORDER BY o.createdAt DESC")
    Page<CommentEntity> findCommentsByCommentParentIdAndPostPostId(String postId, Long commentParentId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM CommentEntity c WHERE c.commentId = ?1 OR c.commentEntityParent.commentId = ?1")
    void deleteCommentAndReplies(Long commentId);

    @Query("SELECT COUNT(o) FROM CommentEntity o WHERE  DATE(o.createdAt) >=?1 AND DATE(o.createdAt) <=?2")
    Long countCommentInDateRange(LocalDate startDate, LocalDate endDate);
}
