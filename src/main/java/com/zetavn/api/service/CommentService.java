package com.zetavn.api.service;

import com.zetavn.api.payload.request.CommentRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.payload.response.Paginate;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CommentService {
    ApiResponse<Paginate<List<CommentResponse>>> getCommentsByPostId(String postId, Integer pageNumber, Integer pageSize);
    ApiResponse<Paginate<List<CommentResponse>>> getParentCommentsByPostId(String postId, Long parentCommentId, Integer pageNumber, Integer pageSize);
    ApiResponse<CommentResponse> addComment(String postId, CommentRequest commentRequest);
    ApiResponse<CommentResponse> addSubComment(String postId, Long parentId, CommentRequest commentRequest);
    ApiResponse<CommentResponse> updateComment(Long commentId, CommentRequest commentRequest);
    ApiResponse<?> deleteComment(Long commentId);
}
