package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.response.OverallUserResponse;

public class OverallUserMapper {
    public static OverallUserResponse entityToDto(UserEntity userEntity) {
        OverallUserResponse userMentionDto = new OverallUserResponse();
        userMentionDto.setId(userEntity.getUserId());
        userMentionDto.setUsername(userEntity.getUsername());
        userMentionDto.setDisplay(userEntity.getFirstName()+" "+userEntity.getLastName());
        userMentionDto.setFirstName(userEntity.getFirstName());
        userMentionDto.setLastName(userEntity.getLastName());
        userMentionDto.setAvatar(userEntity.getAvatar());
        userMentionDto.setPoster(userEntity.getPoster());
        return userMentionDto;
    }
}
