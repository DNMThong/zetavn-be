package com.zetavn.api.service.impl;

import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.payload.request.CommentRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.model.entity.CommentEntity;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.mapper.CommentMapper;
import com.zetavn.api.repository.CommentRepository;
import com.zetavn.api.repository.PostRepository;
import com.zetavn.api.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final CommentMapper commentMapper;
    @Autowired
    CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public ApiResponse<List<CommentResponse>> getCommentsByPostId(String postId) {
        Optional<PostEntity> p = postRepository.findById(postId);
        if(p.isEmpty()) {
            throw new NotFoundException("Not found post with postId: " + postId);
        }
        List<CommentEntity> comments = commentRepository.findCommentsByPostId(postId);
        List<CommentResponse> commentDTO = comments.stream().map(commentMapper::entityToCommentResponse).collect(Collectors.toList());
        return ApiResponse.success(HttpStatus.OK, "Get comment by postId success! ", commentDTO);
    }

    @Override
    public ApiResponse<List<CommentResponse>> getParentCommentsByPostId(String postId, Long parentCommentId) {
        Optional<PostEntity> p = postRepository.findById(postId);
        if(p.isEmpty()) {
            throw new NotFoundException("Not found post with postId: " + postId);
        }
        List<CommentEntity> comments = this.commentRepository.findCommentsByCommentParentIdAndPostPostId(postId, parentCommentId);
        List<CommentResponse> commentDTO = comments.stream().map(commentMapper::entityToCommentResponse).collect(Collectors.toList());
        return ApiResponse.success(HttpStatus.OK, "Get parent comment by postId success", commentDTO);
    }

    @Override
    public ApiResponse<CommentResponse> addComment(String postId, CommentRequest commentRequest) {
        Optional<PostEntity> p = postRepository.findById(postId);
        if(p.isEmpty()) {
            throw new NotFoundException("Not found post with postId: " + postId);
        }
        CommentEntity comment = commentMapper.commentRequestToEntity(commentRequest);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        PostEntity post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found with ID: " + postId));
        comment.setPostEntity(post);

        CommentEntity saveComment = commentRepository.save(comment);

        return ApiResponse.success(HttpStatus.OK, "", commentMapper.entityToCommentResponse(saveComment));
    }

    @Override
    public ApiResponse<CommentResponse> updateComment(Long commentId, String content, String mediaPath) {
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found with commentID: " + commentId));
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setMediaPath(mediaPath);
        comment.setContent(content);
        CommentEntity saveComment = commentRepository.save(comment);
        return ApiResponse.success(HttpStatus.OK, "Update comment success", commentMapper.entityToCommentResponse(saveComment));
    }

    @Override
    public boolean deleteComment(Long commentId) {
        Optional<CommentEntity> c = commentRepository.findById(commentId);
        if(c.isEmpty()) {
            throw new NotFoundException("Not found comment!");
        }
        commentRepository.deleteById(commentId);
        Optional<CommentEntity> comment = commentRepository.findById(commentId);
        return comment.isEmpty();
    }
}
