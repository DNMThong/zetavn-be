package com.zetavn.api.model.mapper;

import com.zetavn.api.payload.request.CommentRequest;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.model.entity.CommentEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.repository.CommentRepository;
import com.zetavn.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class CommentMapper {

    private CommentRepository commentRepository;
    private UserRepository userRepository;


    @Autowired
    public CommentMapper(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }
    public CommentMapper() {

    }

    public CommentResponse entityToCommentResponse(CommentEntity comment) {
        CommentResponse commentDTO = new CommentResponse();
        commentDTO.setCommentId(comment.getCommentId());
        commentDTO.setCommentEntityParentId(comment.getCommentEntityParent() == null ? null : comment.getCommentEntityParent().getCommentId());
        commentDTO.setContent(comment.getContent());

        commentDTO.setUserId(comment.getUserEntity().getUserId());
        commentDTO.setMediaPath(comment.getMediaPath());

        return commentDTO;
    }
//    public CommentRequest entityToCommentRequest(CommentEntity comment) {
//        CommentRequest commentDTO = new CommentRequest();
//        commentDTO.setCommentId(comment.getCommentId());
//        commentDTO.setCommentEntityParentId(comment.getCommentEntityParent() == null ? null : comment.getCommentEntityParent().getCommentId());
//        commentDTO.setContent(comment.getContent());
//
//        commentDTO.setUserId(comment.getUserEntity().getUserId());
//        commentDTO.setMediaPath(comment.getMediaPath());
//
//        return commentDTO;
//    }

    public CommentEntity commentRequestToEntity(CommentRequest commentRequest) {
        CommentEntity comment = new CommentEntity();
        comment.setCommentId(commentRequest.getCommentId());

        Long parentCommentId = commentRequest.getCommentEntityParentId();
        if (parentCommentId != null) {
            CommentEntity cId = commentRepository.getCommentEntityByCommentParentId(parentCommentId);
            comment.setCommentEntityParent(cId);
        } else {
            comment.setCommentEntityParent(null);
        }

        comment.setContent(commentRequest.getContent());

        Optional<UserEntity> user = userRepository.findById(commentRequest.getUserId());
        comment.setUserEntity(user.orElse(null));

        comment.setMediaPath(commentRequest.getMediaPath());

        return comment;
    }
//    CommentResponse mapCommentRequestToResponse(CommentRequest commentRequest) {
//        CommentResponse commentResponse = new CommentResponse();
//        commentResponse.setCommentId(commentRequest.getCommentId());
//        commentResponse.setCommentEntityParentId(commentRequest.getCommentEntityParentId());
//        commentResponse.setUserId(commentRequest.getUserId());
//        commentResponse.setContent(commentRequest.getContent());
//        commentResponse.setMediaPath(commentRequest.getMediaPath());
//        return commentResponse;
//    }
}
