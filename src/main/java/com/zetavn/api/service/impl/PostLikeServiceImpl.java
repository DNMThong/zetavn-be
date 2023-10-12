package com.zetavn.api.service.impl;

import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.entity.PostLikeEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.PostLikeRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.PostLikeOfPostResponse;
import com.zetavn.api.payload.response.PostLikeResponse;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.repository.PostLikeRepository;
import com.zetavn.api.repository.PostRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostLikeServiceImpl implements PostLikeService {
    @Autowired
    PostLikeRepository postLikeRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public ApiResponse<?> getListLikedUserOfPost(String postId) {
        try{
            PostEntity post = postRepository.findById(postId).orElseThrow(NullPointerException::new);
            List<PostLikeEntity> postLikeEntityList = postLikeRepository.getAllByPostEntity(post);
            PostLikeOfPostResponse postLikeOfPostResponse = new PostLikeOfPostResponse();
            List<UserResponse> userResponses = new ArrayList<UserResponse>();
            for (PostLikeEntity u:postLikeEntityList) {
                userResponses.add(UserMapper.userEntityToUserResponse(u.getUserEntity()));
            }
            Integer likeNumber = postLikeRepository.countByPostEntity(post);
            postLikeOfPostResponse.setLikeCount(likeNumber);
            postLikeOfPostResponse.setLikedUsers(userResponses);
            return ApiResponse.success(HttpStatus.OK,"Get list liked user of post success",postLikeOfPostResponse);

        }catch (Exception e){
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
        }

    @Override
    public ApiResponse<?> createPostLike(PostLikeRequest postLikeRequest) {
        try{
            PostEntity post = postRepository.findById(postLikeRequest.getPostId()).orElseThrow(NullPointerException::new);
            UserEntity user = userRepository.findById(postLikeRequest.getUserId()).orElseThrow(NullPointerException::new);
            PostLikeEntity postLike = new PostLikeEntity();
            postLike.setPostEntity(post);
            postLike.setUserEntity(user);
            return ApiResponse.success(HttpStatus.OK,"Create post-like success",postLikeRepository.save(postLike));

        }catch (Exception e){
            return  ApiResponse.error(HttpStatus.BAD_REQUEST,e.getMessage());
        }
     }

    @Override
    public ApiResponse<?> removePostLike(PostLikeRequest postLikeRequest) {
        try{
            PostEntity post = postRepository.findById(postLikeRequest.getPostId()).orElseThrow(NullPointerException::new);
            UserEntity user = userRepository.findById(postLikeRequest.getUserId()).orElseThrow(NullPointerException::new);
            PostLikeEntity postLike = postLikeRepository.findPostLikeEntityByPostEntityAndUserEntity(post,user);
            postLikeRepository.delete(postLike);
            return ApiResponse.success(HttpStatus.OK,"Delete post-like success",null);

        }catch (Exception e){
            return ApiResponse.error(HttpStatus.BAD_REQUEST,e.getMessage());
        }
     }

}
