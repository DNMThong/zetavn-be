package com.zetavn.api.service;

import com.zetavn.api.model.entity.PostLikeEntity;
import com.zetavn.api.payload.request.PostLikeRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.PostLikeOfPostResponse;



public interface PostLikeService {
       ApiResponse<?> getListLikedUserOfPost(String postId);
       ApiResponse<?> createPostLike (PostLikeRequest postLikeRequest, String userId);
       ApiResponse<?> removePostLike (PostLikeRequest postLikeRequest, String userId);
       ApiResponse<?> checkPostLike(String userId, String postId);

}
