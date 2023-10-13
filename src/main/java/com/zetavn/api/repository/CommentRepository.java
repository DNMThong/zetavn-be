package com.zetavn.api.repository;

import com.zetavn.api.model.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("SELECT c FROM CommentEntity c WHERE c.commentId = :commentParentId")
    CommentEntity getCommentEntityByCommentParentId(Long commentParentId);
    @Query("select o from CommentEntity o where o.postEntity.postId = ?1 AND o.commentEntityParent.commentId IS NULL")
    List<CommentEntity> findCommentsByPostId(String postId);

    @Query("select o from CommentEntity o where o.postEntity.postId = ?1 AND o.commentEntityParent.commentId= ?2")
    List<CommentEntity> findCommentsByCommentParentIdAndPostPostId(String postId, Long commentParentId);
}
