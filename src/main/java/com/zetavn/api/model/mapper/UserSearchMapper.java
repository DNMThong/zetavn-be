package com.zetavn.api.model.mapper;

import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.response.OverallUserResponse;
import com.zetavn.api.payload.response.UserSearchResponse;

public class UserSearchMapper {
    public static UserSearchResponse entityToDto(UserEntity userEntity) {
        UserSearchResponse user = new UserSearchResponse();
        OverallUserResponse overallUserResponse = OverallUserMapper.entityToDto(userEntity);
        return user;
    }
}
