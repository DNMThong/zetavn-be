package com.zetavn.api.service.impl;

import com.zetavn.api.enums.PostStatusEnum;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.dto.PostDto;
import com.zetavn.api.model.entity.*;
import com.zetavn.api.model.mapper.PostMapper;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.PostMediaRequest;
import com.zetavn.api.payload.request.PostMentionRequest;
import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.repository.*;
import com.zetavn.api.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public PostDto getPostById(String postId) {
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("No posts found with ID: " + postId));
        return PostMapper.entityToDto(postEntity);
    }

    @Override
    public ApiResponse<List<PostDto>> getAllPostByUserId(String userID) {
        return ApiResponse.success(HttpStatus.OK, "Get all post by userId success", PostMapper.entityListToDtoList(postRepository.getAllPostByUserId(userID)));
    }

    @Override
    public ApiResponse<PostDto> createPost(PostRequest postRequest) {
        try {

            LocalDateTime currentDateTime = LocalDateTime.now();
            String uuid = generateRandomUUID();

            UserEntity user = userRepository.findById(postRequest.getUserId()).orElseThrow(() -> new NotFoundException("No user found with ID: " + postRequest.getUserId()));
            PostEntity post = new PostEntity();
            post.setPostId(uuid);
            post.setUserEntity(user);
            post.setContent(postRequest.getContent());
            post.setAccessModifier(postRequest.getAccessModifier());
            post.setStatus(PostStatusEnum.ACTIVE);
            post.setIsDeleted(false);
            post.setCreatedAt(currentDateTime);
            post.setUpdatedAt(currentDateTime);

            if (postRequest.getActivityId() != null) {
                PostActivityEntity postActivity = postActivityRepository.getActivityById(postRequest.getActivityId());
                post.setPostActivityEntity(postActivity);
                postRepository.save(post);
            }

            if (postRequest.getMedias() != null) {
                List<PostMediaEntity> newList = new ArrayList<>();
                for (PostMediaRequest postMedia : postRequest.getMedias()) {
                    PostMediaEntity postMediaEntity = new PostMediaEntity();
                    postMediaEntity.setPostEntity(post);
                    postMediaEntity.setMediaPath(postMedia.getMediaPath());
                    postMediaEntity.setMediaType(postMedia.getMediaType());
                    postMediaEntity.setUserEntity(user);
                    newList.add(postMediaEntity);
                }
                postMediaRepository.saveAll(newList);
                post.setPostMediaEntityList(newList);
            }

            if (postRequest.getMentions() != null) {
                List<PostMentionEntity> newList = new ArrayList<>();
                for (PostMentionRequest postMention : postRequest.getMentions()) {
                    PostMentionEntity postMentionEntity = new PostMentionEntity();
                    postMentionEntity.setUserEntity(userRepository.findById(postMention.getUserId()).orElseThrow(() -> new NotFoundException("No user found with ID: " + postMention.getUserId())));
                    postMentionEntity.setPostEntity(post);
                    newList.add(postMentionEntity);
                }
                postMentionRepository.saveAll(newList);
                post.setPostMentionEntityList(newList);
            }
            return ApiResponse.success(HttpStatus.CREATED, "Created post success", PostMapper.entityToDto(post));
        } catch (Exception e) {
            System.out.println(e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "errr");
        }
    }

    @Override
    public ApiResponse<PostDto> updatePost(String postId, PostRequest updatedPostRequest) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        PostEntity existingPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("No posts found with ID: " + postId));
        UserEntity existingUser = userRepository.findById(updatedPostRequest.getUserId()).orElseThrow(() -> new NotFoundException("No user found with ID: " + updatedPostRequest.getUserId()));

        existingPost.setContent(updatedPostRequest.getContent());
        existingPost.setAccessModifier(updatedPostRequest.getAccessModifier());
        existingPost.setUpdatedAt(currentDateTime);

        if (updatedPostRequest.getActivityId() != null) {
            PostActivityEntity postActivity = postActivityRepository.getActivityById(updatedPostRequest.getActivityId());
            existingPost.setPostActivityEntity(postActivity);
            postRepository.save(existingPost);
        } else {
            existingPost.setPostActivityEntity(null);
            postRepository.save(existingPost);
        }

        if (updatedPostRequest.getMedias() != null) {
            List<PostMediaRequest> updatedMediaList = updatedPostRequest.getMedias();
            List<PostMediaEntity> currentMediaList = existingPost.getPostMediaEntityList();
            List<PostMediaEntity> mediaToRemove = new ArrayList<>(currentMediaList);

            for (PostMediaRequest updatedMedia : updatedMediaList) {
                PostMediaEntity currentMedia = mediaToRemove.stream()
                        .filter(media -> media.getMediaPath().equals(updatedMedia.getMediaPath()))
                        .findFirst()
                        .orElse(null);

                if (currentMedia != null) {
                    currentMedia.setPostEntity(existingPost);
                    currentMedia.setMediaPath(updatedMedia.getMediaPath());
                    currentMedia.setMediaType(updatedMedia.getMediaType());
                    currentMedia.setUserEntity(existingUser);
                    mediaToRemove.remove(currentMedia);
                }
            }
            postMediaRepository.deleteAll(mediaToRemove);

            for (PostMediaRequest updatedMedia : updatedMediaList) {
                boolean mediaExists = currentMediaList.stream()
                        .anyMatch(media -> media.getMediaPath().equals(updatedMedia.getMediaPath()));

                if (!mediaExists) {
                    PostMediaEntity newMedia = new PostMediaEntity();
                    newMedia.setPostEntity(existingPost);
                    newMedia.setMediaPath(updatedMedia.getMediaPath());
                    newMedia.setMediaType(updatedMedia.getMediaType());
                    newMedia.setUserEntity(existingUser);
                    postMediaRepository.save(newMedia);
                }
            }
        }

        if (updatedPostRequest.getMentions() != null) {
            List<PostMentionRequest> updatedMentionList = updatedPostRequest.getMentions();
            List<PostMentionEntity> currentMentionList = existingPost.getPostMentionEntityList();
            List<PostMentionEntity> mentionToRemove = new ArrayList<>(currentMentionList);

            for (PostMentionRequest updatedMention : updatedMentionList) {
                PostMentionEntity currentMention = mentionToRemove.stream()
                        .filter(mention -> mention.getUserEntity().getUserId().equals(updatedMention.getUserId()))
                        .findFirst()
                        .orElse(null);

                if (currentMention != null) {
                    currentMention.setPostEntity(existingPost);
                    currentMention.setUserEntity(userRepository.findById(updatedMention.getUserId()).orElseThrow(() -> new NotFoundException("No user found with ID: " + updatedMention.getUserId())));
                    mentionToRemove.remove(currentMention);
                }
            }
            postMentionRepository.deleteAll(mentionToRemove);

            for (PostMentionRequest updateMention : updatedMentionList) {
                boolean mentionExists = currentMentionList.stream()
                        .anyMatch(mention -> mention.getUserEntity().getUserId().equals(updateMention.getUserId()));

                if (!mentionExists) {
                    PostMentionEntity newMention = new PostMentionEntity();
                    newMention.setPostEntity(existingPost);
                    newMention.setUserEntity(userRepository.findById(updateMention.getUserId()).orElseThrow(() -> new NotFoundException("No user found with ID: " + updateMention.getUserId())));
                    postMentionRepository.save(newMention);
                }
            }
        }

        return ApiResponse.success(HttpStatus.OK, "Updated post success", PostMapper.entityToDto(existingPost));
    }

    @Override
    public ApiResponse<String> deletePost(String postId) {
        Optional<PostEntity> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            PostEntity existingPost = optionalPost.get();
            postRepository.delete(existingPost);
            return ApiResponse.success(HttpStatus.OK, "The post has been successfully deleted.", "postId: " + postId);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "No posts found with ID: " + postId);
        }
    }

    @Override
    public ApiResponse<Paginate<List<PostDto>>> getAllPostByUserFollow(String userId, Integer pageNumber, Integer pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<PostEntity> posts = postRepository.getAllPostByUserFollow(userId, pageable);
            List<PostEntity> postList = posts.getContent();
            List<PostDto> postDtoList = PostMapper.entityListToDtoList(postList);

            Paginate<List<PostDto>> dataResponse = new Paginate<>(
                    posts.getNumber(),
                    posts.getSize(),
                    posts.getTotalElements(),
                    posts.getTotalPages(),
                    posts.isLast(),
                    postDtoList
            );

            return ApiResponse.success(HttpStatus.OK, "Success", dataResponse);
        } catch (Exception e) {
            System.out.println(e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
        }
    }

}
