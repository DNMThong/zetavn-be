package com.zetavn.api.service.impl;

import com.zetavn.api.model.entity.*;
import com.zetavn.api.payload.request.PostMediaRequest;
import com.zetavn.api.payload.request.PostMentionRequest;
import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.PostResponse;
import com.zetavn.api.repository.*;
import com.zetavn.api.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zetavn.api.model.mapper.PostMapper.mapToResponse;
import static com.zetavn.api.utils.UUIDGenerator.generateRandomUUID;

@Service
@Slf4j
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostActivityRepository postActivityRepository;

    @Autowired
    private PostMediaRepository postMediaRepository;

    @Autowired
    private PostMentionRepository postMentionRepository;

    @Override
    public ApiResponse<?> getPostById(String postId) {
        Optional<PostEntity> optional = postRepository.findById(postId);
        return ApiResponse.success(HttpStatus.OK, "Get post success", optional);
    }

    @Override
    public ApiResponse<?> getAllPostByUserId(String userID) {
        return ApiResponse.success(HttpStatus.OK, "Get all post by userId success", postRepository.getAllPostByUserId(userID));
    }

    @Override
    public ApiResponse<PostResponse> createPost(PostRequest postRequest) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String uuid = generateRandomUUID();

        UserEntity user = userRepository.findById(postRequest.getUserId()).orElseThrow(NullPointerException::new);
        PostEntity post = new PostEntity();
        post.setPostId(uuid);
        post.setUserEntity(user);
        post.setContent(postRequest.getContent());
        post.setAccessModifier(postRequest.getAccessModifier());
        post.setIsDeleted(false);
        post.setCreatedAt(currentDateTime);
        post.setUpdatedAt(currentDateTime);
        postRepository.save(post);

        if (postRequest.getPostActivity() != null) {
            PostActivityEntity postActivity = new PostActivityEntity();
            postActivity.setPostEntity(post);
            postActivity.setActivity(postRequest.getPostActivity().getActivity());
            postActivity.setDescription(postRequest.getPostActivity().getDescription());
            postActivity.setActivityIconPath(postRequest.getPostActivity().getActivityIconPath());
            postActivity.setPostActivityParentId(postRequest.getPostActivity().getPostActivityParentId());
            postActivityRepository.save(postActivity);
        }

        if (postRequest.getPostMedias() != null) {
            List<PostMediaEntity> newList = new ArrayList<>();
            for (PostMediaRequest postMedia : postRequest.getPostMedias()) {
                PostMediaEntity postMediaEntity = new PostMediaEntity();
                postMediaEntity.setPostEntity(post);
                postMediaEntity.setMediaPath(postMedia.getMediaPath());
                postMediaEntity.setMediaType(postMedia.getMediaType());
                postMediaEntity.setUserEntity(user);
                newList.add(postMediaEntity);
            }
            postMediaRepository.saveAll(newList);
        }

        if (postRequest.getPostMentions() != null) {
            List<PostMentionEntity> newList = new ArrayList<>();
            for (PostMentionRequest postMention : postRequest.getPostMentions()) {
                PostMentionEntity postMentionEntity = new PostMentionEntity();
                postMentionEntity.setUserEntity(user); // chưa có func gọi tìm user theo userId
                postMentionEntity.setPostEntity(post);
                newList.add(postMentionEntity);
            }
            postMentionRepository.saveAll(newList);
        }

        return ApiResponse.success(HttpStatus.OK, "Create post success", mapToResponse(postRequest));
    }
}
