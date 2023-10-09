package com.zetavn.api.service.impl;

import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.payload.request.CommentRequest;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.model.entity.CommentEntity;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.mapper.CommentMapper;
import com.zetavn.api.repository.CommentRepository;
import com.zetavn.api.repository.PostRepository;
import com.zetavn.api.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentMapper commentMapper;


    @Override
    public List<CommentResponse> getCommentsByPostId(String postId) {
        List<CommentEntity> comments = commentRepository.findCommentsByPostId(postId);
        List<CommentResponse> commentDTO = comments.stream().map(comment -> commentMapper.entityToCommentResponse(comment)).collect(Collectors.toList());
        return commentDTO;
    }

    @Override
    public List<CommentResponse> getParentCommentsByPostId(String postId, Long parentCommentId) {
        List<CommentEntity> comments = this.commentRepository.findCommentsByCommentParentIdAndPostPostId(postId, parentCommentId);
        List<CommentResponse> commentDTO = comments.stream().map(commentMapper::entityToCommentResponse).collect(Collectors.toList());
        return commentDTO;
    }

    @Override
    public CommentResponse addComment(String postId, CommentRequest commentRequest) {
        CommentEntity comment = commentMapper.commentRequestToEntity(commentRequest);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        PostEntity post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found with ID: " + postId));
        comment.setPostEntity(post);

        CommentEntity saveComment = commentRepository.save(comment);

        return commentMapper.entityToCommentResponse(saveComment);
    }

    @Override
    public CommentResponse updateComment(CommentRequest commentRequest) {
        CommentEntity comment = commentRepository.findById(commentRequest.getCommentId()).orElseThrow(() -> new NotFoundException("Comment not found with commentID: " + commentRequest.getCommentId()));
        CommentEntity newComment = commentMapper.commentRequestToEntity(commentRequest);
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setMediaPath(newComment.getMediaPath());
        comment.setContent(newComment.getContent());
        CommentEntity saveComment = commentRepository.save(comment);

        return commentMapper.entityToCommentResponse(saveComment);
    }

    @Override
    public boolean deleteComment(Long commentId) {
        if(commentId == null) {
            throw new NullPointerException(commentId + "is null");
        }
        commentRepository.deleteById(commentId);
        Optional<CommentEntity> comment = commentRepository.findById(commentId);
        return comment.isEmpty();
    }
}
