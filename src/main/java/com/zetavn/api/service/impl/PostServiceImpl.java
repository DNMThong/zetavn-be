package com.zetavn.api.service.impl;

import com.zetavn.api.enums.PostStatusEnum;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.dto.PostAdminDto;
import com.zetavn.api.model.dto.PostDto;
import com.zetavn.api.model.dto.PostMediaDto;
import com.zetavn.api.model.entity.*;
import com.zetavn.api.model.mapper.PostMapper;
import com.zetavn.api.model.mapper.PostMediaMapper;
import com.zetavn.api.payload.request.PostMediaRequest;
import com.zetavn.api.payload.request.PostMentionRequest;
import com.zetavn.api.payload.request.PostRequest;
import com.zetavn.api.payload.request.UpdatePostForAdminRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.Paginate;
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
import java.util.*;

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

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Override
    public PostDto getPostById(String postId) {
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("No posts found with ID: " + postId));
        return PostMapper.entityToDto(postEntity);
    }

    @Override
    public ApiResponse<Paginate<List<PostDto>>> getAllPostByUserId(String userId,Integer pageNumber, Integer pageSize) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> {throw new NotFoundException("Not found post by username: "+userId);});

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<PostEntity> posts = postRepository.getAllPostByUserId(userEntity.getUserId(),pageable);
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
        return ApiResponse.success(HttpStatus.OK, "Get all post by userId success", dataResponse);
    }

    @Override
    public ApiResponse<PostDto> createPost(PostRequest postRequest, String userId) {
        try {
            LocalDateTime currentDateTime = LocalDateTime.now();
            String uuid = generateRandomUUID();

            UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No user found with ID: " + userId));
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
            } else {
                post.setPostActivityEntity(null);
            }
            postRepository.save(post);

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
    public ApiResponse<PostDto> updatePost(String postId, PostRequest updatedPostRequest, String userId) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            PostEntity existingPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("No posts found with ID: " + postId));
            UserEntity existingUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No user found with ID: " + userId));

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
    public ApiResponse<?> removePost(String userId, String postId) {
        Optional<PostEntity> p = postRepository.findById(postId);
        if(p.isPresent()) {
            PostEntity post = p.get();
            if(post.getUserEntity().getUserId().equals(userId)) {
                post.setIsDeleted(true);
                post.setUpdatedAt(LocalDateTime.now());
                postRepository.save(post);
                return ApiResponse.success(HttpStatus.OK, "delete success", "");
            } else {
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "post not by userId");
            }
        } else {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Not found post");
        }
    }

    @Override
    public ApiResponse<Paginate<List<PostDto>>> getAllPostByUserFollow(String userId, Integer pageNumber, Integer pageSize) {

        Map<String,String> friendsOfFriendMap = new HashMap<>();
        List<String> friends = new ArrayList<>();

        userRepository.findFriendsByUser(userId).forEach(friend -> {
            friends.add(friend.getUserId());
            userRepository.findFriendsByUser(friend.getUserId()).forEach(friendOfFriend -> {
                friendsOfFriendMap.put(friendOfFriend.getUserId(),"");
            });
        });
        List<String> friendsOfFriend = new ArrayList<>(friendsOfFriendMap.keySet());

        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<PostEntity> posts = postRepository.getAllPostByUserList(userId, friends,friendsOfFriend, pageable);
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

    @Override
    public ApiResponse<Paginate<List<PostMediaDto>>> getPostsWithMediaByUserId(String userId,String type, Integer pageNumber, Integer pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<PostMediaEntity> posts = postRepository.getPostsWithMediaByUserId(userId, type, pageable);
            List<PostMediaEntity> postList = posts.getContent();
            List<PostMediaDto> postDtoList = PostMediaMapper.entityListToDtoList(postList);
            Paginate<List<PostMediaDto>> dataResponse = new Paginate<>(
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

    @Override
    public ApiResponse<?> getAllPostsForAdminByStatus(String status, Integer pageNumber, Integer pageSize){
        return switch (status) {
            case "active" ->
                    ApiResponse.success(HttpStatus.OK, "Get all post active", pageableUserForAdmin(PostStatusEnum.ACTIVE,pageNumber,pageSize));
            case "locked" ->
                    ApiResponse.success(HttpStatus.OK, "Get all post locked",  pageableUserForAdmin(PostStatusEnum.LOCKED,pageNumber,pageSize));
           default -> getAllPostForAdmin(pageNumber,pageSize);
        };
    }
    @Override
    public ApiResponse<?> pageableUserForAdmin(PostStatusEnum postStatusEnum, Integer pageNumber, Integer pageSize) {
        log.info("Try to find Posts by status {} at page number {} and page size {}", postStatusEnum, pageNumber, pageSize);
        if (pageNumber < 0 || pageSize < 0) {
            log.error("Error Logging: pageNumber {} < 0 || pageSize {} < 0 with status {}", pageNumber, pageSize, postStatusEnum);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Page number and page size must be positive");
        } else {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<PostEntity> posts = postRepository.getAllPostsForAdminByStatus(postStatusEnum, pageable);
            if (pageNumber > posts.getTotalPages()) {
                log.error("Error Loggingssss: pageNumber: {} is out of total_page: {}", pageNumber, posts.getNumber(),posts.getTotalPages());
                throw new InvalidParameterException("pageNumber is out of total Page");
            }
            try {
                List<PostEntity> postEntities = posts.getContent();
                List<PostAdminDto> postAdminDtos = postEntities.stream().map(PostMapper::entityToPostAdminDto).toList();
                Paginate<List<PostAdminDto>> dataResponse = new Paginate<>();
                dataResponse.setData(postAdminDtos);
                dataResponse.setPageNumber(posts.getNumber());
                dataResponse.setPageSize(posts.getSize());
                dataResponse.setTotalElements(posts.getTotalElements());
                dataResponse.setTotalPages(posts.getTotalPages());
                dataResponse.setLastPage(posts.isLast());
                return ApiResponse.success(HttpStatus.OK, "Success", dataResponse);
            } catch (Exception e) {
                log.error("Error Logging: pageNumber: {}, pageSize: {}, pageable: {}, error_message: {}", pageNumber, pageSize, pageable, e.getMessage());
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
            }
        }
    }
    @Override
    public ApiResponse<?> getAllPostForAdmin(Integer pageNumber, Integer pageSize){
        log.info("Try to find Users at page number {} and page size {}", pageNumber, pageSize);
        if (pageNumber < 0 || pageSize < 0) {
            log.error("Error Logging: pageNumber {} < 0 || pageSize {} < 0", pageNumber, pageSize);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Page number and page size must be positive");
        } else {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<PostEntity> posts = postRepository.findAllPosts(pageable);
            try {
                List<PostEntity> postEntities = posts.getContent();
                List<PostAdminDto> userResponses = postEntities.stream().map(PostMapper::entityToPostAdminDto).toList();
                Paginate<List<PostAdminDto>> dataResponse = new Paginate<>();
                dataResponse.setData(userResponses);
                dataResponse.setPageNumber(posts.getNumber());
                dataResponse.setPageSize(posts.getSize());
                dataResponse.setTotalElements(posts.getTotalElements());
                dataResponse.setTotalPages(posts.getTotalPages());
                dataResponse.setLastPage(posts.isLast());
                return ApiResponse.success(HttpStatus.OK, "Success", dataResponse);
            } catch (Exception e) {
                log.error("Error Logging: pageNumber: {}, pageSize: {}, pageable: {}, error_message: {}", pageNumber, pageSize, pageable, e.getMessage());
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
            }
        }
    }
    @Override
    public ApiResponse<?> updatePostForAdmin(UpdatePostForAdminRequest request){
        if(request.getStatus().equals("active")){
            PostEntity post=postRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("No posts found with ID: " + request.getId()));
            post.setStatus(PostStatusEnum.ACTIVE);
            post.setUpdatedAt(LocalDateTime.now());
            PostEntity postUpdated= postRepository.save(post);
            return ApiResponse.success(HttpStatus.OK,"Update success post",postUpdated);
        }
        if(request.getStatus().equals("locked")){
            PostEntity post=postRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("No posts found with ID: " + request.getId()));
            post.setStatus(PostStatusEnum.LOCKED);
            post.setUpdatedAt(LocalDateTime.now());
            PostEntity postUpdated= postRepository.save(post);
            return ApiResponse.success(HttpStatus.OK,"Update success post",postUpdated);
        }
        return ApiResponse.error(HttpStatus.BAD_REQUEST,"Update error",null);
    }
    @Override
    public ApiResponse<?> getOnePostForAdmin(String id){
        Optional<PostEntity> postEntity = postRepository.findById(id);
        if(postEntity.isPresent()){
            return ApiResponse.success(HttpStatus.OK,"success", PostMapper.entityToPostAdminDto(postEntity.get()));
        }
        return ApiResponse.error(HttpStatus.NOT_FOUND,"Not found post",null);
    }

    @Override
    public ApiResponse<?> lockPostForAdmin(String id) {
        PostEntity post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Lock post failed! Post does not exist"));
        post.setStatus(PostStatusEnum.LOCKED);
        post.setUpdatedAt(LocalDateTime.now());
        PostEntity postUpdated= postRepository.save(post);

        return ApiResponse.success(HttpStatus.OK,"Lock post success",postUpdated);
    }
}
