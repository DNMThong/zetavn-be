package com.zetavn.api.service.impl;

import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.entity.UserInfoEntity;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.UserInfoRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.UserInfoResponse;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.repository.FriendshipRepository;
import com.zetavn.api.repository.PostRepository;
import com.zetavn.api.repository.UserInfoRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.UserInfoService;
import com.zetavn.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserService userService;

    @Override
    public ApiResponse<?> create(UserInfoRequest request) {
        return null;
    }

    @Override
    public ApiResponse<?> update(String userId, UserInfoRequest request) {
        if (userId == null) {
            log.error("Error logging: UserId null");
            return ApiResponse.error(HttpStatus.FORBIDDEN, "User id is required");
        } else {
            UserInfoEntity userInfo = userInfoRepository.findByUserEntity_UserId(userId);
            UserEntity userEntity = userRepository.findById(userId).get();
            log.info("User information: {}", userInfo);
            if (userInfo == null) {
                log.error("Not found info ìn database: userId: {}", userId);
                return ApiResponse.error(HttpStatus.FORBIDDEN, "Update failed! Not found user");
            } else {
                userEntity.setEmail(request.getEmail());
                userEntity.setUsername(request.getUsername());
                userEntity.setPhone(request.getPhone());
                userEntity.setFirstName(request.getFirstName());
                userEntity.setLastName(request.getLastName());
                userEntity = userRepository.save(userEntity);

                userInfo.setAboutMe(request.getAboutMe());
                userInfo.setGenderEnum(request.getGenderEnum());
                userInfo.setBirthday(request.getBirthday());
                userInfo.setLivesAt(request.getLivesAt());
                userInfo.setWorksAt(request.getWorksAt());
                userInfo.setStudiedAt(request.getStudiedAt());
                userInfo.setUpdatedAt(LocalDateTime.now());
                log.info("New user info: {} - try to save", userInfo);
                UserInfoEntity info = userInfoRepository.save(userInfo);
                UserResponse userResponse = UserMapper.userInfoToUserResponse(userEntity.getUserInfo());
                return ApiResponse.success(HttpStatus.OK, "Update user information success", userResponse);
            }
        }
    }

    @Override
    public ApiResponse<?> remove() {
        return null;
    }

    @Override
    public ApiResponse<?> getUserInfoByUsername(String username) {
        UserEntity user = userRepository.findUserEntityByUsername(username);
        if (user == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Not found user");
        } else {
            String userId = user.getUserId();
            UserResponse response = UserMapper.userInfoToUserResponse(userInfoRepository.findByUserEntity_UserId(userId));
            response.getInformation().setTotalFriends(friendshipRepository.countFriends(userId));
            response.getInformation().setTotalPosts(postRepository.countPostEntityByUserEntityUserId(userId));
            response.getInformation().setCountLikesOfPosts(postRepository.getTotalLikesByUserId(userId));
            System.out.println("User: " + response.toString());
            return ApiResponse.success(HttpStatus.OK, "Get user information success", response);
        }
    }
}
