package com.zetavn.api.service;

import com.zetavn.api.enums.PostStatusEnum;
import com.zetavn.api.model.dto.PostDto;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.request.UpdatePostForAdminRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.payload.response.PostResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    PostDto getPostById(String postId);

    ApiResponse<Paginate<List<PostDto>>> getAllPostByUserId(String userId,Integer pageNumber, Integer pageSize);

    ApiResponse<PostDto> createPost(PostRequest postRequest, String userId);

    ApiResponse<PostDto> updatePost(String postId, PostRequest updatedPostRequest, String userId);

    ApiResponse<?> removePost(String userId, String postId);

    ApiResponse<Paginate<List<PostDto>>> getAllPostByUserFollow(String userId, Integer pageNumber, Integer pageSize);

    ApiResponse<?> getAllPostsForAdminByStatus(String status, Integer pageNumber, Integer pageSize);

    ApiResponse<?> pageableUserForAdmin(PostStatusEnum postStatusEnum, Integer pageNumber, Integer pageSize);

    ApiResponse<?> getAllPostForAdmin(Integer pageNumber, Integer pageSize);

    ApiResponse<?> updatePostForAdmin(UpdatePostForAdminRequest request);

    ApiResponse<?> getOnePostForAdmin(String id);

    ApiResponse<?> getPostsWithMediaByUserId(String userId,String type, Integer pageNumber, Integer pageSize);
}
