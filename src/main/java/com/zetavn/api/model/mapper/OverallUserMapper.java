package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.response.OverallUserResponse;

public class OverallUserMapper {
    public static OverallUserResponse entityToDto(UserEntity userEntity) {
        OverallUserResponse userMentionDto = new OverallUserResponse();
        userMentionDto.setUserId(userEntity.getUserId());
        userMentionDto.setUsername(userEntity.getUsername());
        userMentionDto.setFirstName(userEntity.getFirstName());
        userMentionDto.setLastName(userEntity.getLastName());
        userMentionDto.setAvatar(userEntity.getAvatar());
        userMentionDto.setPoster(userEntity.getPoster());
        return userMentionDto;
    }
}
