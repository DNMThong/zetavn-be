package com.zetavn.api.service;

import com.zetavn.api.model.dto.PostDto;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.PostResponse;

public interface PostService {
    PostDto getPostById(String postId);

    ApiResponse<?> getAllPostByUserId(String userID);

    ApiResponse<PostDto> createPost(PostRequest postRequest);

    ApiResponse<PostDto> updatePost(String postId, PostRequest updatedPostRequest);

    ApiResponse<String> deletePost(String postId);
}
