package com.zetavn.api.service;

import com.zetavn.api.model.dto.PostDto;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.payload.response.PostResponse;

import java.util.List;

public interface PostService {
    PostDto getPostById(String postId);

    ApiResponse<?> getAllPostByUserId(String userID);

    ApiResponse<PostDto> createPost(PostRequest postRequest);

    ApiResponse<PostDto> updatePost(String postId, PostRequest updatedPostRequest);

    ApiResponse<?> deletePost(String postId);

    ApiResponse<Paginate<List<PostDto>>> getAllPostByUserFollow(String userId, Integer pageNumber, Integer pageSize);
}
