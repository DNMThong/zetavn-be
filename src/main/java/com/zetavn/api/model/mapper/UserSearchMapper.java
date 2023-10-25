package com.zetavn.api.model.mapper;

import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.response.OverallUserResponse;
import com.zetavn.api.payload.response.UserSearchResponse;

public class UserSearchMapper {
    public static UserSearchResponse entityToDto(UserEntity userEntity) {
        UserSearchResponse user = new UserSearchResponse();
        user.setId(userEntity.getUserId());
        user.setUsername(userEntity.getUsername());
        user.setDisplay(userEntity.getFirstName()+" "+userEntity.getLastName());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setAvatar(userEntity.getAvatar());
        user.setPoster(userEntity.getPoster());

        return user;
    }
}
