package com.zetavn.api.service;

import com.zetavn.api.model.entity.PostLikeEntity;
import com.zetavn.api.payload.request.PostLikeRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.PostLikeOfPostResponse;



public interface PostLikeService {
       ApiResponse<PostLikeOfPostResponse> getListLikedUserOfPost(String postId);
       ApiResponse<PostLikeEntity> createPostLike (PostLikeRequest postLikeRequest);
       ApiResponse<PostLikeEntity> removePostLike (PostLikeRequest postLikeRequest);
}
