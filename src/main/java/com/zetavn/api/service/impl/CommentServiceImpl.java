package com.zetavn.api.service.impl;

import com.zetavn.api.enums.PostNotificationEnum;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.dto.PostDto;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.request.CommentRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.model.entity.CommentEntity;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.mapper.CommentMapper;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.repository.CommentRepository;
import com.zetavn.api.repository.PostRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.CommentService;
import com.zetavn.api.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Slf4j

public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private NotificationService notificationService;

    @Autowired
    CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ApiResponse<Paginate<List<CommentResponse>>> getCommentsByPostId(String postId, Integer pageNumber, Integer pageSize) {
        Optional<PostEntity> p = postRepository.findById(postId);
        if(p.isEmpty()) {
            throw new NotFoundException("Not found post with postId: " + postId);
        }
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<CommentEntity> comments =  commentRepository.findCommentsByPostId(postId, pageable);
            List<CommentEntity> cm =  comments.getContent();
            List<CommentResponse> c = cm.stream().map(CommentMapper::entityToCommentResponse).collect(Collectors.toList());
            Paginate<List<CommentResponse>> dataResponse = new Paginate<>(
                    comments.getNumber(),
                    comments.getSize(),
                    comments.getTotalElements(),
                    comments.getTotalPages(),
                    comments.isLast(),
                    c
            );
            return ApiResponse.success(HttpStatus.OK, "Get comment by postId success! ", dataResponse);

        } catch (Exception e) {
            System.out.println(e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
        }
    }

    @Override
    public ApiResponse<Paginate<List<CommentResponse>>> getParentCommentsByPostId(String postId, Long parentCommentId, Integer pageNumber, Integer pageSize) {
        Optional<PostEntity> p = postRepository.findById(postId);
        if(p.isEmpty()) {
            throw new NotFoundException("Not found post with postId: " + postId);
        }
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<CommentEntity> comments =  commentRepository.findCommentsByCommentParentIdAndPostPostId(postId, parentCommentId, pageable);
            List<CommentEntity> cm = comments.getContent();
            List<CommentResponse> c = cm.stream().map(CommentMapper::entityToCommentResponse).collect(Collectors.toList());
            Paginate<List<CommentResponse>> dataResponse = new Paginate<>(
                    comments.getNumber(),
                    comments.getSize(),
                    comments.getTotalElements(),
                    comments.getTotalPages(),
                    comments.isLast(),
                    c
            );
            return ApiResponse.success(HttpStatus.OK, "Get comment by postId success! ", dataResponse);

        } catch (Exception e) {
            System.out.println(e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
        }
    }


    @Override
    public ApiResponse<CommentResponse> addSubComment(String postId, Long parentId, CommentRequest commentRequest) {
        try {
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Optional<PostEntity> p = postRepository.findById(postId);
            if(p.isEmpty()) {
                throw new NotFoundException("Not found post with postId: " + postId);
            }
            CommentEntity comment = new CommentEntity();

            Optional<CommentEntity> p_comment = commentRepository.findById(parentId);
            comment.setCommentEntityParent(p_comment.get());

            comment.setContent(commentRequest.getContent());
            comment.setMediaPath(commentRequest.getPath());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());

            PostEntity post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found with ID: " + postId));
            comment.setPostEntity(post);

            Optional<UserEntity> user = userRepository.findById(id);
            comment.setUserEntity(user.get());

            CommentEntity saveComment = commentRepository.save(comment);
            return ApiResponse.success(HttpStatus.OK, "", CommentMapper.entityToCommentResponse(saveComment));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Override
    public ApiResponse<CommentResponse> addComment(String postId, CommentRequest commentRequest) {
       try {
           String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
           Optional<PostEntity> p = postRepository.findById(postId);
           if(p.isEmpty()) {
               throw new NotFoundException("Not found post with postId: " + postId);
           }
           CommentEntity comment = new CommentEntity();
           comment.setCommentEntityParent(null);
           comment.setContent(commentRequest.getContent());
           comment.setMediaPath(commentRequest.getPath());
           comment.setCreatedAt(LocalDateTime.now());
           comment.setUpdatedAt(LocalDateTime.now());

           PostEntity post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found with ID: " + postId));
           comment.setPostEntity(post);

           Optional<UserEntity> user = userRepository.findById(id);
           comment.setUserEntity(user.get());

           CommentEntity saveComment = commentRepository.save(comment);
           notificationService.createNotification(user.get().getUserId(), post.getUserEntity().getUserId(), post.getPostId(), PostNotificationEnum.COMMENT, saveComment.getCommentId());
           return ApiResponse.success(HttpStatus.OK, "", CommentMapper.entityToCommentResponse(saveComment));
       } catch (Exception e){
           return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Override
    public ApiResponse<CommentResponse> updateComment(Long commentId, CommentRequest commentRequest) {
        try {
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            CommentEntity comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found with commentID: " + commentId));
            comment.setUpdatedAt(LocalDateTime.now());
            comment.setMediaPath(commentRequest.getPath());
            comment.setContent(commentRequest.getContent());
            if(!Objects.equals(comment.getUserEntity().getUserId(), id)) {
                return ApiResponse.error(HttpStatus.FORBIDDEN, "You don't have permission to update this comment");
            }
            CommentEntity saveComment = commentRepository.save(comment);
            return ApiResponse.success(HttpStatus.OK, "Update comment success", CommentMapper.entityToCommentResponse(saveComment));
        } catch (Exception e){
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public ApiResponse<?> deleteComment(Long commentId) {
        Optional<CommentEntity> c = commentRepository.findById(commentId);
        if(c.isEmpty()) {
            throw new NotFoundException("Not found comment!");
        }

        commentRepository.deleteCommentAndReplies(commentId);
        notificationService.deleteNotification(commentId, PostNotificationEnum.COMMENT);
        return ApiResponse.success(HttpStatus.OK, "Delete success", null);
    }
}
