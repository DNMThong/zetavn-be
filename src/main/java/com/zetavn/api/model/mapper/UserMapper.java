package com.zetavn.api.model.mapper;

import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.response.UserResponse;

public class UserMapper {
    public static UserResponse userEntityToUserResponse(UserEntity userEntity){

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(userEntity.getUserId());
        userResponse.setIsDeleted(userEntity.getIsDeleted());
        userResponse.setIsAuthorized(userEntity.getIsAuthorized());
        userResponse.setToken(userEntity.getToken());
        userResponse.setFirstName(userEntity.getFirstName());
        userResponse.setPoster(userEntity.getPoster());
        userResponse.setLastName(userEntity.getLastName());
        userResponse.setStatus(userEntity.getStatus());
        userResponse.setCreatedAt(userEntity.getCreatedAt());
        userResponse.setAvatar(userEntity.getAvatar());
        userResponse.setPhone(userEntity.getPhone());
        userResponse.setEmail(userEntity.getEmail());
        userResponse.setRole(userEntity.getRole());
        userResponse.setAvatar(userEntity.getAvatar());
        userResponse.setUpdatedAt(userEntity.getUpdatedAt());
        userResponse.setUsername(userEntity.getUsername());
        return  userResponse;
    }

    public static UserEntity userResponseToUserEntity(UserResponse userResponse) {
        UserEntity user = new UserEntity();
        user.setEmail(null);
        return user;
    }

}
