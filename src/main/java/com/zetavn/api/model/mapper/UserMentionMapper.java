package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.model.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class UserMentionMapper {
    public static UserMentionDto entityToDto(UserEntity userEntity) {
        UserMentionDto userMentionDto = new UserMentionDto();
        userMentionDto.setId(userEntity.getUserId());
        userMentionDto.setUsername(userEntity.getUsername());
        userMentionDto.setFirstName(userEntity.getFirstName());
        userMentionDto.setLastName(userEntity.getLastName());
        userMentionDto.setDisplay(userEntity.getFirstName()+" "+userEntity.getLastName());
        userMentionDto.setAvatar(userEntity.getAvatar());
        userMentionDto.setPoster(userEntity.getPoster());


        return userMentionDto;
    }

    public static List<UserMentionDto> entityListToDtoList(List<UserEntity> userEntities) {
        return userEntities.stream()
                .map(UserMentionMapper::entityToDto)
                .collect(Collectors.toList());
    }
}
