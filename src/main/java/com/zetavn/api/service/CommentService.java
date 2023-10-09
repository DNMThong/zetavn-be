package com.zetavn.api.service;

import com.zetavn.api.payload.request.CommentRequest;
import com.zetavn.api.payload.response.CommentResponse;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CommentService {
    List<CommentResponse> getCommentsByPostId(String postId);
    List<CommentResponse> getParentCommentsByPostId(String postId, Long parentCommentId);
    CommentResponse addComment(String postId, CommentRequest commentRequest);
    CommentResponse updateComment(CommentRequest commentRequest);
    boolean deleteComment(Long commentId);
}
