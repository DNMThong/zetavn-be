package com.zetavn.api.service.impl;

import com.zetavn.api.model.entity.UserInfoEntity;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.UserInfoRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.UserResponse;
import com.zetavn.api.repository.UserInfoRepository;
import com.zetavn.api.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

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
            log.info("User information: {}", userInfo);
            if (userInfo == null) {
                log.error("Not found info ìn database: userId: {}", userId);
                return ApiResponse.error(HttpStatus.FORBIDDEN, "Update failed! Not found user");
            } else {
                userInfo.setAboutMe(request.getAboutMe());
                userInfo.setGenderEnum(request.getGenderEnum());
                userInfo.setBirthday(request.getBirthday());
                userInfo.setLivesAt(request.getLivesAt());
                userInfo.setWorksAt(request.getWorksAt());
                userInfo.setStudiedAt(request.getStudiedAt());
                userInfo.setUpdatedAt(LocalDateTime.now());
                log.info("New user info: {} - try to save", userInfo);
                userInfoRepository.save(userInfo);
                return ApiResponse.success(HttpStatus.OK, "Update user information success", userInfo);
            }
        }
    }

    @Override
    public ApiResponse<?> remove() {
        return null;
    }

    @Override
    public ApiResponse<?> getUserInfoByUserId(String userId) {

        return ApiResponse.success(HttpStatus.OK, "Get user information success", UserMapper.userInfoToUserResponse(userInfoRepository.findByUserEntity_UserId(userId)));
    }
}
