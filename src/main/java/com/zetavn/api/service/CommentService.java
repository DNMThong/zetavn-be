package com.zetavn.api.service;

import com.zetavn.api.payload.request.CommentRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.CommentResponse;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CommentService {
    ApiResponse<List<CommentResponse>> getCommentsByPostId(String postId);
    ApiResponse<List<CommentResponse>> getParentCommentsByPostId(String postId,Long parentCommentId);
    ApiResponse<CommentResponse> addComment(String postId, Long parentId, CommentRequest commentRequest);
    ApiResponse<CommentResponse> updateComment(Long commentId, CommentRequest commentRequest);
    boolean deleteComment(Long commentId);
}
