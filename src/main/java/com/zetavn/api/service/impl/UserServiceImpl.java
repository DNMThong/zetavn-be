package com.zetavn.api.service.impl;

import com.cloudinary.Api;
import com.zetavn.api.enums.*;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.dto.UserAdminDto;
import com.zetavn.api.model.entity.FriendshipEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.entity.UserInfoEntity;
import com.zetavn.api.model.mapper.MessageMapper;
import com.zetavn.api.model.mapper.OverallUserMapper;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.request.UserInfoRequest;
import com.zetavn.api.payload.request.UserUpdateRequest;
import com.zetavn.api.payload.response.*;
import com.zetavn.api.repository.*;
import com.zetavn.api.service.UserInfoService;
import com.zetavn.api.service.UserService;
import com.zetavn.api.utils.UUIDGenerator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserInfoService userInfoService;




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
    public UserResponse update(UserUpdateRequest userUpdateRequest) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
    public ApiResponse<?> getAllUsersByKeyword(String keyword, Integer pageNumber, Integer pageSize) {
        String sourceId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
    public ApiResponse<?> getAllFriendsByKeyword(String keyword, Integer pageNumber, Integer pageSize) {
        String sourceId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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
    public ApiResponse<?> getStrangersByKeyword(String keyword, Integer pageNumber, Integer pageSize) {
        String sourceId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
    public ApiResponse<UserResponse> updateAvatar(String avatarPath) {
        String sourceId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
    public ApiResponse<UserResponse> updatePoster(String posterPath) {
        String sourceId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
    public ApiResponse<List<UserContactResponse>> getUserContacts() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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

    @Override
    public ApiResponse<?> getAllUserForAdminByStatus(String status, Integer pageNumber, Integer pageSize) {
        return switch (status) {
            case "active" -> pageableUserForAdmin(UserStatusEnum.ACTIVE, pageNumber, pageSize);
            case "locked" -> pageableUserForAdmin(UserStatusEnum.LOCKED, pageNumber, pageSize);
            case "suspended" -> pageableUserForAdmin(UserStatusEnum.SUSPENDED, pageNumber, pageSize);
            default -> getAllUserForAdmin(pageNumber, pageSize);
        };
    }

    @Override
    public ApiResponse<?> pageableUserForAdmin(UserStatusEnum userStatusEnum, Integer pageNumber, Integer pageSize) {
        log.info("Try to find Users by status {} at page number {} and page size {}", userStatusEnum, pageNumber, pageSize);
        if (pageNumber < 0 || pageSize < 0) {
            log.error("Error Logging: pageNumber {} < 0 || pageSize {} < 0 with status {}", pageNumber, pageSize, userStatusEnum);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Page number and page size must be positive");
        } else {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<UserEntity> users = userRepository.findByUserEntityByStatus(userStatusEnum, pageable);
            if (pageNumber > users.getTotalPages()) {
                log.error("Error Logging: pageNumber: {} is out of total_page: {}", pageNumber, users.getNumber());
                throw new InvalidParameterException("pageNumber is out of total Page");
            }
            try {
                List<UserEntity> userEntities = users.getContent();
                System.out.println(userEntities);
                List<UserAdminDto> userAdminDtos = userEntities.stream().map(UserMapper::userEntityToUserAdminDto).toList();
                Paginate<List<UserAdminDto>> dataResponse = new Paginate<>();
                dataResponse.setData(userAdminDtos);
                dataResponse.setPageNumber(users.getNumber());
                dataResponse.setPageSize(users.getSize());
                dataResponse.setTotalElements(users.getTotalElements());
                dataResponse.setTotalPages(users.getTotalPages());
                dataResponse.setLastPage(users.isLast());
                return ApiResponse.success(HttpStatus.OK, "Success", dataResponse);
            } catch (Exception e) {
                log.error("Error Logging: pageNumber: {}, pageSize: {}, pageable: {}, error_message: {}", pageNumber, pageSize, pageable, e.getMessage());
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
            }
        }
    }

    @Override
    public ApiResponse<?> getAllUserForAdmin(Integer pageNumber, Integer pageSize) {

        log.info("Try to find Users at page number {} and page size {}", pageNumber, pageSize);
        if (pageNumber < 0 || pageSize < 0) {
            log.error("Error Logging: pageNumber {} < 0 || pageSize {} < 0", pageNumber, pageSize);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Page number and page size must be positive");
        } else {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<UserEntity> users = userRepository.findAllUser(pageable);
            if (pageNumber > users.getTotalPages()) {
                log.error("Error Logging: pageNumber: {} is out of total_page: {}", pageNumber, users.getNumber());
                throw new InvalidParameterException("pageNumber is out of total Page");
            }
            try {
                List<UserEntity> userEntities = users.getContent();
                List<UserAdminDto> userResponses = userEntities.stream().map(UserMapper::userEntityToUserAdminDto).toList();
                Paginate<List<UserAdminDto>> dataResponse = new Paginate<>();
                dataResponse.setData(userResponses);
                dataResponse.setPageNumber(users.getNumber());
                dataResponse.setPageSize(users.getSize());
                dataResponse.setTotalElements(users.getTotalElements());
                dataResponse.setTotalPages(users.getTotalPages());
                dataResponse.setLastPage(users.isLast());
                return ApiResponse.success(HttpStatus.OK, "Success", dataResponse);
            } catch (Exception e) {
                log.error("Error Logging: pageNumber: {}, pageSize: {}, pageable: {}, error_message: {}", pageNumber, pageSize, pageable, e.getMessage());
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
            }
        }
    }

    @Override
    public ApiResponse<?> createForAdmin(UserAdminDto userAdminDto) {
        UserEntity userEntity = new UserEntity();
        if (!existUserByEmail(userAdminDto.getEmail())) {
            log.warn("Found Email in database: {}", userAdminDto.getEmail());
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Email is used", null);
        }
        if (!existUserByUsername(userAdminDto.getUsername())) {
            log.warn("Found Username in database: {}", userAdminDto.getUsername());
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Username is used", null);
        }
        if (userAdminDto.getInformation() == null) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Create failed! User-info is null");
        }
        if (userAdminDto.getInformation().getBirthday() == null) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Create failed! Birthday is null");
        }
        if (userAdminDto.getInformation().getGenderEnum() == null) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Create failed! Gender is null");
        }
        userAdminDto.setId(UUIDGenerator.generateRandomUUID());
        userAdminDto.setCreatedAt(LocalDateTime.now());
        userAdminDto.setUpdatedAt(LocalDateTime.now());
        UserEntity _user = userRepository.save(UserMapper.userAdminDtoToUserEntity(userAdminDto, userEntity));
        if (_user != null) {
            UserInfoEntity userInfo = new UserInfoEntity();
            userInfo.setAboutMe(userAdminDto.getInformation().getAboutMe());
            userInfo.setGenderEnum(userAdminDto.getInformation().getGenderEnum());
            userInfo.setBirthday(userAdminDto.getInformation().getBirthday());
            userInfo.setLivesAt(userAdminDto.getInformation().getLivesAt());
            userInfo.setWorksAt(userAdminDto.getInformation().getWorksAt());
            userInfo.setStudiedAt(userAdminDto.getInformation().getStudiedAt());
            userInfo.setGenderEnum(userAdminDto.getInformation().getGenderEnum());
            userInfo.setUserEntity(_user);
            userInfo.setCreatedAt(LocalDateTime.now());
            userInfo.setUpdatedAt(LocalDateTime.now());
            log.info("try to save userinfo: birthday: {} - gender: {}", userInfo.getBirthday(), userInfo.getGenderEnum());
            UserInfoEntity userInfoEntity = userInfoRepository.save(userInfo);
            _user.setUserInfo(userInfoEntity);
        } else {
            throw new IllegalArgumentException("Creat user not success");
        }

        return ApiResponse.success(HttpStatus.OK, "Create user success", UserMapper.userEntityToUserAdminDto(_user));
    }

    @Override
    public ApiResponse<UserAdminDto> updateForAdmin(UserAdminDto userAdminDto) {
        Optional<UserEntity> userEntity = userRepository.findById(userAdminDto.getId());
        if (userAdminDto.getUsername() == null || userAdminDto.getUsername().isEmpty()) {
            log.error("Error Logging: UserService - existUserByUsername - Missing username arg");
            throw new IllegalArgumentException("UserService - existUserByUsername - Missing username arg");
        }
        if (userEntity.isPresent()) {
            try {
                userAdminDto.setUpdatedAt(LocalDateTime.now());
                userAdminDto.setCreatedAt(userEntity.get().getCreatedAt());
                UserEntity _user = userRepository.save(UserMapper.userAdminDtoToUserEntity(userAdminDto, userEntity.get()));
                if (userAdminDto.getInformation() == null) {
                    return ApiResponse.error(HttpStatus.BAD_REQUEST, "Create failed! User-info is null");
                }
                if (userAdminDto.getInformation().getBirthday() == null) {
                    return ApiResponse.error(HttpStatus.BAD_REQUEST, "Create failed! Birthday is null");
                }
                if (userAdminDto.getInformation().getGenderEnum() == null) {
                    return ApiResponse.error(HttpStatus.BAD_REQUEST, "Create failed! Gender is null");
                }
                UserInfoRequest userInfoRequest = new UserInfoRequest();
                userInfoRequest.setBirthday(userAdminDto.getInformation().getBirthday());
                userInfoRequest.setAboutMe(userAdminDto.getInformation().getAboutMe());
                userInfoRequest.setGenderEnum(userAdminDto.getInformation().getGenderEnum());
                userInfoRequest.setBirthday(userAdminDto.getInformation().getBirthday());
                userInfoRequest.setLivesAt(userAdminDto.getInformation().getLivesAt());
                userInfoRequest.setWorksAt(userAdminDto.getInformation().getWorksAt());
                userInfoRequest.setStudiedAt(userAdminDto.getInformation().getStudiedAt());
                userInfoService.update(userEntity.get().getUserId(), userInfoRequest);
                log.info("Update success user");
                return ApiResponse.success(HttpStatus.OK, "Update user success", UserMapper.userEntityToUserAdminDto(_user));
            } catch (Exception e) {
                log.warn("Email or username are used");
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Email or username are used", userAdminDto);
            }
        }
        log.warn("Not found userId in database: {}", userAdminDto.getId());
        return ApiResponse.error(HttpStatus.NOT_FOUND, "Not found user", null);
    }

    @Override
    public ApiResponse<?> removeForAdmin(String id, boolean isDeleted) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if (userEntity.isPresent()) {
            log.info("User removed:", userEntity.get().getUserId());
            userEntity.get().setIsDeleted(isDeleted);
            userRepository.save(userEntity.get());
            return ApiResponse.success(HttpStatus.OK, "User is deleted", null);
        }
        return ApiResponse.error(HttpStatus.NOT_FOUND, "Not found userId", id);
    }

    @Override
    public ApiResponse<?> getOneUserForAdmin(String id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if (userEntity.isPresent()) {
            return ApiResponse.success(HttpStatus.OK, "User is deleted", UserMapper.userEntityToUserAdminDto(userEntity.get()));
        }
        return ApiResponse.error(HttpStatus.NOT_FOUND, "Not found User", null);
    }
}
