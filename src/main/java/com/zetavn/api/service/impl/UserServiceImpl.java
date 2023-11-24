package com.zetavn.api.service.impl;

import com.cloudinary.Api;
import com.zetavn.api.enums.*;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.FriendshipEntity;
import com.zetavn.api.model.entity.MessageEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.MessageMapper;
import com.zetavn.api.model.mapper.OverallUserMapper;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.request.UserUpdateRequest;
import com.zetavn.api.payload.response.*;
import com.zetavn.api.repository.ActivityLogRepository;
import com.zetavn.api.repository.FriendshipRepository;
import com.zetavn.api.repository.PostRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.UserService;
import com.zetavn.api.utils.UUIDGenerator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;


    @Override
    public ApiResponse<?> create(SignUpRequest signUpRequest) {
        log.info("Try to create User in database");
        if (existUserByUsername(signUpRequest.getEmail())) {
            log.error("User exist in DB: {}", signUpRequest.getEmail());
            return ApiResponse.error(HttpStatus.CONFLICT, "Email have been taken");
        } else {

            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(UUIDGenerator.generateRandomUUID());
            userEntity.setUsername(userEntity.getUserId());
            userEntity.setEmail(signUpRequest.getEmail());
            userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setUpdatedAt(LocalDateTime.now());
            userEntity.setStatus(UserStatusEnum.ACTIVE);
            userEntity.setLastName(signUpRequest.getLastName());
            userEntity.setFirstName(signUpRequest.getFirstName());
            UserEntity _user = userRepository.save(userEntity);
            log.info("Create user in database success: {}", _user.getEmail());
            return ApiResponse.success(HttpStatus.OK, "Register success", UserMapper.userEntityToUserResponse(_user));
        }
    }

    @Override
    public ApiResponse<UserResponse> getUserByEmail(String email) {
        return null;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserResponse> page = userRepository.findAll().stream().map(UserMapper::userEntityToUserResponse).toList();
        return page;
    }

    @Override
    public UserResponse update(String userId, UserUpdateRequest userUpdateRequest) {
        UserEntity user = userRepository.findById(userId).get();
        user.setEmail(userUpdateRequest.getEmail());
        user.setUsername(userUpdateRequest.getUsername());
        user.setPhone(userUpdateRequest.getPhone());
        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());
        user = userRepository.save(user);
        return UserMapper.userInfoToUserResponse(user.getUserInfo());
    }

    @Override
    public ApiResponse<UserResponse> remove(String userId) {
        return null;
    }

    @Override
    public ApiResponse<UserResponse> getUserByEmailOrUsername(String username, String email) {
        return null;
    }


    private boolean existUserByEmail(String email) {
        log.info("Trying to check user exist by email: {}", email);
        if (email == null || email.equals("")) {
            log.error("Error Logging: UserService - existUserByEmail - Missing email arg");
            throw new IllegalArgumentException("UserService - existUserByEmail - Missing email arg");
        } else {
            UserEntity user = userRepository.findUserEntityByEmail(email);
            log.info("Found email in database: {}", email);
            return user != null;
        }
    }

    private boolean existUserByUsername(String username) {
        log.info("Trying to check user exist by username: {}", username);
        if (username == null || username.equals("")) {
            log.error("Error Logging: UserService - existUserByUsername - Missing username arg");
            throw new IllegalArgumentException("UserService - existUserByUsername - Missing username arg");
        } else {
            UserEntity user = userRepository.findUserEntityByUsername(username);
            log.info("Found username in database: {}", username);
            return user != null;
        }
    }
    @Override
    public ApiResponse<?> getAllUsersByKeyword(String sourceId, String keyword, Integer pageNumber, Integer pageSize) {
        log.info("Try to find Users by keyword {} at page number {} and page size {}", keyword, pageNumber, pageSize);
        if (pageNumber < 0 || pageSize < 0) {
            log.error("Error Logging: pageNumber {} < 0 || pageSize {} < 0 with keyword {}", pageNumber, pageSize, keyword);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Page number and page size must be positive");
        } else {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<UserEntity> users = userRepository.findUserEntityByKeyword(sourceId, keyword, pageable);
            if (pageNumber > users.getTotalPages()) {
                log.error("Error Logging: pageNumber: {} is out of total_page: {}", pageNumber, users.getNumber());
                throw new InvalidParameterException("pageNumber is out of total Page");
            }
            try {
                List<UserEntity> userEntities = users.getContent();
                List<UserSearchResponse> userSearchResponses = new ArrayList<>();
                for(UserEntity u : userEntities) {
                    UserSearchResponse user = new UserSearchResponse();
                    OverallUserResponse userResponses = OverallUserMapper.entityToDto(u);
                    user.setUser(userResponses);
                    user.setTotalFriends(friendshipRepository.countFriends(u.getUserId()));
                    user.setTotalPosts(postRepository.countPostEntityByUserEntityUserId(u.getUserId()));
                    user.setCountLikesOfPosts(postRepository.getTotalLikesByUserId(u.getUserId()));
                    FriendshipEntity statusFriendsEnum = friendshipRepository.checkFriendshipStatus(sourceId, u.getUserId());
                    if(statusFriendsEnum != null) {
                        if (statusFriendsEnum.getStatus().equals(FriendStatusEnum.ACCEPTED)) {
                            user.setStatus(StatusFriendsEnum.FRIEND);
                        } else if (statusFriendsEnum.getStatus().equals(FriendStatusEnum.PENDING)) {
                            if (statusFriendsEnum.getSenderUserEntity().getUserId().equals(sourceId)) {
                                user.setStatus(StatusFriendsEnum.SENDER);
                            } else {
                                user.setStatus(StatusFriendsEnum.RECEIVER);
                            }
                        } else {
                            user.setStatus(StatusFriendsEnum.NONE);
                        }
                    } else {
                        user.setStatus(StatusFriendsEnum.NONE);
                    }
                    userSearchResponses.add(user);
                }
                Paginate<List<UserSearchResponse>> dataResponse = new Paginate<>();
                dataResponse.setData(userSearchResponses);
                dataResponse.setPageNumber(users.getNumber());
                dataResponse.setPageSize(users.getSize());
                dataResponse.setTotalElements(users.getTotalElements());
                dataResponse.setTotalPages(users.getTotalPages());
                dataResponse.setLastPage(users.isLast());
                return ApiResponse.success(HttpStatus.OK, "Success", dataResponse);
            } catch (Exception e) {
                log.error("Error Logging: pageNumber: {}, pageSize: {}, keyword: {}, error_message: {}, param_id: {}", pageNumber, pageSize, keyword, e.getMessage(), sourceId);
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
            }
        }
    }

    @Override
    public ApiResponse<?> getAllFriendsByKeyword(String sourceId, String keyword, Integer pageNumber, Integer pageSize) {
        log.info("Try to find Users by keyword {} at page number {} and page size {}", keyword, pageNumber, pageSize);
        if (pageNumber < 0 || pageSize < 0) {
            log.error("Error Logging: pageNumber {} < 0 || pageSize {} < 0 with keyword {}", pageNumber, pageSize, keyword);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Page number and page size must be positive");
        } else {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Optional<UserEntity> source = userRepository.findById(sourceId);
            Set<String> friendIdList = new HashSet<>();
//            friendIdList.addAll(source.get().getUserReceiverList().stream().map(u -> u.getSenderUserEntity().getUserId()).toList());
//            friendIdList.addAll(source.get().getUserSenderList().stream().map(u -> u.getReceiverUserEntity().getUserId()).toList());

            source.get().getUserReceiverList().forEach((item) -> {
                if(item.getStatus()==FriendStatusEnum.ACCEPTED) {
                    friendIdList.add(item.getSenderUserEntity().getUserId());
                }
            });

            source.get().getUserSenderList().forEach((item) -> {
                if(item.getStatus()==FriendStatusEnum.ACCEPTED) {
                    friendIdList.add(item.getReceiverUserEntity().getUserId());
                }
            });

            Page<UserEntity> users = userRepository.findUserEntitiesByFriendList(sourceId, friendIdList.stream().toList(), keyword, pageable);
            if (pageNumber > users.getTotalPages()) {
                log.error("Error Logging: pageNumber: {} is out of total_page: {}", pageNumber, users.getNumber());
                throw new InvalidParameterException("pageNumber is out of total Page");
            }
            try {
                List<UserEntity> userEntities = users.getContent();
                List<UserSearchResponse> userSearchResponses = new ArrayList<>();
                for(UserEntity u : userEntities) {
                    UserSearchResponse user = new UserSearchResponse();
                    OverallUserResponse userResponses = OverallUserMapper.entityToDto(u);
                    user.setUser(userResponses);
                    user.setTotalFriends(friendshipRepository.countFriends(u.getUserId()));
                    user.setTotalPosts(postRepository.countPostEntityByUserEntityUserId(u.getUserId()));
                    user.setCountLikesOfPosts(postRepository.getTotalLikesByUserId(u.getUserId()));
                    user.setStatus(StatusFriendsEnum.FRIEND);
                    userSearchResponses.add(user);
                }
                Paginate<List<UserSearchResponse>> dataResponse = new Paginate<>();
                dataResponse.setData(userSearchResponses);
                dataResponse.setPageNumber(users.getNumber());
                dataResponse.setPageSize(users.getSize());
                dataResponse.setTotalElements(users.getTotalElements());
                dataResponse.setTotalPages(users.getTotalPages());
                dataResponse.setLastPage(users.isLast());
                return ApiResponse.success(HttpStatus.OK, "Success", dataResponse);
            } catch (Exception e) {
                log.error("Error Logging: pageNumber: {}, pageSize: {}, keyword: {}, error_message: {}, param_id: {}", pageNumber, pageSize, keyword, e.getMessage(), sourceId);
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
            }
        }
    }

    @Override
    public ApiResponse<?> getStrangersByKeyword(String sourceId, String keyword, Integer pageNumber, Integer pageSize) {
        log.info("Try to find Users by keyword {} at page number {} and page size {}", keyword, pageNumber, pageSize);
        if (pageNumber < 0 || pageSize < 0) {
            log.error("Error Logging: pageNumber {} < 0 || pageSize {} < 0 with keyword {}", pageNumber, pageSize, keyword);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Page number and page size must be positive");
        } else {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Optional<UserEntity> source = userRepository.findById(sourceId);
            Set<String> friendIdList = new HashSet<>();
            friendIdList.addAll(source.get().getUserReceiverList().stream().map(u -> u.getSenderUserEntity().getUserId()).toList());
            friendIdList.addAll(source.get().getUserSenderList().stream().map(u -> u.getReceiverUserEntity().getUserId()).toList());
            Page<UserEntity> users = userRepository.findStrangersByKeyword(sourceId, friendIdList.stream().toList(), keyword, pageable);
            if (pageNumber > users.getTotalPages()) {
                log.error("Error Logging: pageNumber: {} is out of total_page: {}", pageNumber, users.getNumber());
                throw new InvalidParameterException("pageNumber is out of total Page");
            }
            try {
                List<UserEntity> userEntities = users.getContent();
                List<UserSearchResponse> userSearchResponses = new ArrayList<>();
                for(UserEntity u : userEntities) {
                    UserSearchResponse user = new UserSearchResponse();
                    OverallUserResponse userResponses = OverallUserMapper.entityToDto(u);
                    user.setUser(userResponses);
                    user.setTotalFriends(friendshipRepository.countFriends(u.getUserId()));
                    user.setTotalPosts(postRepository.countPostEntityByUserEntityUserId(u.getUserId()));
                    user.setCountLikesOfPosts(postRepository.getTotalLikesByUserId(u.getUserId()));
                    FriendshipEntity statusFriendsEnum = friendshipRepository.checkFriendshipStatus(sourceId, u.getUserId());
                    if(statusFriendsEnum != null) {
                        if (statusFriendsEnum.getStatus().equals(FriendStatusEnum.PENDING)) {
                            if (statusFriendsEnum.getSenderUserEntity().getUserId().equals(sourceId)) {
                                user.setStatus(StatusFriendsEnum.SENDER);
                            } else {
                                user.setStatus(StatusFriendsEnum.RECEIVER);
                            }
                        } else {
                            user.setStatus(StatusFriendsEnum.NONE);
                        }
                    } else {
                        user.setStatus(StatusFriendsEnum.NONE);
                    }
                    userSearchResponses.add(user);
                }
                Paginate<List<UserSearchResponse>> dataResponse = new Paginate<>();
                dataResponse.setData(userSearchResponses);
                dataResponse.setPageNumber(users.getNumber());
                dataResponse.setPageSize(users.getSize());
                dataResponse.setTotalElements(users.getTotalElements());
                dataResponse.setTotalPages(users.getTotalPages());
                dataResponse.setLastPage(users.isLast());
                return ApiResponse.success(HttpStatus.OK, "Success", dataResponse);
            } catch (Exception e) {
                log.error("Error Logging: pageNumber: {}, pageSize: {}, keyword: {}, error_message: {}, param_id: {}", pageNumber, pageSize, keyword, e.getMessage(), sourceId);
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
            }
        }
    }

    @Override
    public ApiResponse<UserResponse> updateAvatar(String sourceId, String avatarPath) {
        Optional<UserEntity> user = userRepository.findById(sourceId);
        if (user.isEmpty())
            throw new NotFoundException("Not found user with userId: " + sourceId);
        else {
            user.get().setAvatar(avatarPath);
            UserResponse response = UserMapper.userInfoToUserResponse(userRepository.save(user.get()).getUserInfo());
            return ApiResponse.success(HttpStatus.OK, "Update avatar success", response);
        }
    }

    @Override
    public ApiResponse<UserResponse> updatePoster(String sourceId, String posterPath) {
        Optional<UserEntity> user = userRepository.findById(sourceId);
        if (user.isEmpty())
            throw new NotFoundException("Not found user with userId: " + sourceId);
        else {
            user.get().setPoster(posterPath);
            UserResponse response = UserMapper.userInfoToUserResponse(userRepository.save(user.get()).getUserInfo());
            return ApiResponse.success(HttpStatus.OK, "Update poster success", response);
        }
    }

    @Override
    public ApiResponse<List<UserContactResponse>> getUserContacts(String userId) {
        UserEntity user = userRepository.findById(userId).orElseGet(() -> {throw new NotFoundException("Not found user with userId: " + userId);});

        Map<String, UserContactResponse> map = new HashMap<String, UserContactResponse>();

        user.getRecieverMessages().forEach(item -> {
            String id = item.getSenderUser().getUserId();
            Integer totalUnreadMessage = 0;
            if(map.containsKey(id)) {
                UserContactResponse userContact = map.get(id);
                if(!item.getStatus().equals(MessageStatusEnum.READ)) {
                    totalUnreadMessage++;
                }
                userContact.setTotalUnreadMessage(totalUnreadMessage);
                userContact.setNewMessage(MessageMapper.entityToDto(item));
                map.put(id,userContact);
            }else {
                UserContactResponse userContactResponse = new UserContactResponse();
                OverallUserPrivateResponse overallUserPrivate = OverallUserMapper.entityToOverallUserPrivate(item.getSenderUser());
                overallUserPrivate.setIsOnline(activityLogRepository.checkUserOnline(overallUserPrivate.getId()));
                userContactResponse.setUser(overallUserPrivate);
                userContactResponse.setNewMessage(MessageMapper.entityToDto(item));
                userContactResponse.setTotalUnreadMessage(0);
                map.put(id,userContactResponse);
            }
        });

        user.getSenderMessages().forEach(item -> {
            String id = item.getRecieverUser().getUserId();
            if(map.containsKey(id)) {
                UserContactResponse userContact = map.get(id);
                if(item.getCreatedAt().isAfter(userContact.getNewMessage().getCreatedAt())) {
                    userContact.setTotalUnreadMessage(0);
                    userContact.setNewMessage(MessageMapper.entityToDto(item));
                    map.put(id,userContact);
                }
            }else {
                UserContactResponse userContactResponse = new UserContactResponse();
                OverallUserPrivateResponse overallUserPrivate = OverallUserMapper.entityToOverallUserPrivate(item.getRecieverUser());
                overallUserPrivate.setIsOnline(activityLogRepository.checkUserOnline(overallUserPrivate.getId()));
                userContactResponse.setUser(overallUserPrivate);
                userContactResponse.setNewMessage(MessageMapper.entityToDto(item));
                userContactResponse.setTotalUnreadMessage(0);
                map.put(id,userContactResponse);
            }
        });





        List<UserContactResponse> userContacts = new ArrayList<>(map.values());

        userContacts.sort(Comparator.comparing(
                userContactResponse -> userContactResponse.getNewMessage().getCreatedAt(),
                Comparator.reverseOrder()
        ));

        return ApiResponse.success(HttpStatus.OK,"Get list user contact success",userContacts);
    }

}
