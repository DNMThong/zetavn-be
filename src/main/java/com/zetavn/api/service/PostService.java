package com.zetavn.api.service;

import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.PostResponse;

public interface PostService {
    ApiResponse<?> getPostById(String postId);

    ApiResponse<?> getAllPostByUserId(String userID);

    ApiResponse<PostResponse> createPost(PostRequest postRequest);
}
