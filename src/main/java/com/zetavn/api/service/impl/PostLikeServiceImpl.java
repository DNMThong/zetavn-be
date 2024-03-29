package com.zetavn.api.service.impl;

import com.zetavn.api.enums.PostNotificationEnum;
import com.zetavn.api.exception.DuplicateRecordException;
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
import com.zetavn.api.service.NotificationService;
import com.zetavn.api.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.NotActiveException;
import java.time.LocalDateTime;
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

    @Autowired
    NotificationService notificationService;

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
    public ApiResponse<?> createPostLike(PostLikeRequest postLikeRequest, String userId) {
        try{
            PostEntity post = postRepository.findById(postLikeRequest.getPostId()).orElseThrow(NullPointerException::new);
            UserEntity user = userRepository.findById(userId).orElseThrow(NullPointerException::new);
            PostLikeEntity postLikeCheck = postLikeRepository.findPostLikeEntityByPostEntityAndUserEntity(post,user);
            if(postLikeCheck!=null){
                throw new DuplicateRecordException("You liked post");
            }
            PostLikeEntity postLike = new PostLikeEntity();
            postLike.setPostEntity(post);
            postLike.setUserEntity(user);
            postLike.setCreatedAt(LocalDateTime.now());
            PostLikeEntity postLikeEntity = postLikeRepository.save(postLike);
            if(userId!=post.getUserEntity().getUserId()) {
                notificationService.createNotification(user.getUserId(), post.getUserEntity().getUserId(), post.getPostId(), PostNotificationEnum.LIKE, postLike.getPostLikeId());
            }
            return ApiResponse.success(HttpStatus.OK,"Create post-like success", postLikeEntity);
        }catch (Exception e){
            return ApiResponse.error(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }


    @Override
    public ApiResponse<?> removePostLike(PostLikeRequest postLikeRequest, String userId) {
        try{
            PostEntity post = postRepository.findById(postLikeRequest.getPostId()).orElseThrow(NullPointerException::new);
            UserEntity user = userRepository.findById(userId).orElseThrow(NullPointerException::new);
            PostLikeEntity postLike = postLikeRepository.findPostLikeEntityByPostEntityAndUserEntity(post,user);
            postLikeRepository.delete(postLike);
            return ApiResponse.success(HttpStatus.OK,"Delete post-like success",null);

        }catch (Exception e){
            return ApiResponse.error(HttpStatus.BAD_REQUEST,e.getMessage());
        }
     }

    @Override
    public ApiResponse<?> checkPostLike(String userId, String postId)  {
        try{
            PostEntity post = postRepository.findById(postId).orElseThrow(NullPointerException::new);
            UserEntity user = userRepository.findById(userId).orElseThrow(NullPointerException::new);
            PostLikeEntity postLikeCheck = postLikeRepository.findPostLikeEntityByPostEntityAndUserEntity(post,user);
            if(postLikeCheck==null){
                return  ApiResponse.success(HttpStatus.OK,"User not like",false);
            }else {
                return  ApiResponse.success(HttpStatus.OK,"User liked",true);
            }
        }catch (Exception e){
            return  ApiResponse.error(HttpStatus.BAD_REQUEST,"Error",e.getMessage());
        }
    }
}
