package com.zetavn.api.service.impl;

import com.zetavn.api.enums.PostStatusEnum;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.dto.PostDto;
import com.zetavn.api.model.entity.*;
import com.zetavn.api.model.mapper.PostMapper;
import com.zetavn.api.payload.request.PostMediaRequest;
import com.zetavn.api.payload.request.PostMentionRequest;
import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.response.ApiResponse;
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
    private CategoryRepository categoryRepository;

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
        postRepository.save(post);

        if (postRequest.getCategoryId() != null) {
            CategoryEntity categoryEntity = categoryRepository.getDetailCategoryById(postRequest.getCategoryId());
            PostActivityEntity postActivity = new PostActivityEntity();
            postActivity.setPostEntity(post);
            postActivity.setCategoryEntity(categoryEntity);
            postActivityRepository.save(postActivity);
            post.setPostActivityEntityList(List.of(postActivity));
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
            post.setPostMediaEntityList(newList);
        }

        if (postRequest.getPostMentions() != null) {
            List<PostMentionEntity> newList = new ArrayList<>();
            for (PostMentionRequest postMention : postRequest.getPostMentions()) {
                PostMentionEntity postMentionEntity = new PostMentionEntity();
                postMentionEntity.setUserEntity(userRepository.findById(postMention.getUserId()).orElseThrow(() -> new NotFoundException("No user found with ID: " + postMention.getUserId())));
                postMentionEntity.setPostEntity(post);
                newList.add(postMentionEntity);
            }
            postMentionRepository.saveAll(newList);
            post.setPostMentionEntityList(newList);
        }

        return ApiResponse.success(HttpStatus.CREATED, "Created post success", PostMapper.entityToDto(post));
    }

    public ApiResponse<PostDto> updatePost(String postId, PostRequest updatedPostRequest) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        PostEntity existingPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("No posts found with ID: " + postId));

        existingPost.setContent(updatedPostRequest.getContent());
        existingPost.setAccessModifier(updatedPostRequest.getAccessModifier());
        existingPost.setUpdatedAt(currentDateTime);
        postRepository.save(existingPost);


        if (updatedPostRequest.getCategoryId() != null) {
            CategoryEntity categoryEntity = categoryRepository.getDetailCategoryById(updatedPostRequest.getCategoryId());
            if (existingPost.getPostActivityEntityList() == null || existingPost.getPostActivityEntityList().isEmpty()) {
                PostActivityEntity postActivity = new PostActivityEntity();
                postActivity.setPostEntity(existingPost);
                postActivity.setCategoryEntity(categoryEntity);
                postActivityRepository.save(postActivity);
                existingPost.setPostActivityEntityList(List.of(postActivity));
            } else {
                PostActivityEntity postActivity = existingPost.getPostActivityEntityList().get(0);
                postActivity.setCategoryEntity(categoryEntity);
                postActivityRepository.save(postActivity);
            }
        } else {
            // Nếu updatedPostRequest không chứa categoryId, bạn có thể xử lý theo cách phù hợp với ứng dụng của bạn
        }

        if (updatedPostRequest.getPostMedias() != null) {
            List<PostMediaRequest> updatedMediaList = updatedPostRequest.getPostMedias();
            List<PostMediaEntity> currentMediaList = existingPost.getPostMediaEntityList();

            // Merge strategy
            for (PostMediaEntity currentMedia : currentMediaList) {
                boolean mediaExists = false;
                for (PostMediaRequest updatedMedia : updatedMediaList) {
                    if (currentMedia.getMediaPath().equals(updatedMedia.getMediaPath())) {
                        // Cập nhật thông tin trường currentMedia dựa trên updatedMedia
                        currentMedia.setMediaPath(updatedMedia.getMediaPath());
                        currentMedia.setMediaType(updatedMedia.getMediaType());
                        // Các trường khác tương tự

                        mediaExists = true;
                        break;
                    }
                }

                // Nếu không tồn tại trong danh sách updatedMedia, xóa bỏ nó
                if (!mediaExists) {
                    postMediaRepository.delete(currentMedia);
                }
            }

            // Thêm mới các media mới
            for (PostMediaRequest updatedMedia : updatedMediaList) {
                if (updatedMedia.getMediaPath() == null) {
                    PostMediaEntity newMedia = new PostMediaEntity();
                    newMedia.setPostEntity(existingPost);
                    newMedia.setMediaPath(updatedMedia.getMediaPath());
                    newMedia.setMediaType(updatedMedia.getMediaType());
                    // Các trường khác tương tự

                    currentMediaList.add(newMedia);
                }
            }
        }


        return ApiResponse.success(HttpStatus.OK, "Updated post success", PostMapper.entityToDto(existingPost));
    }

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


}
