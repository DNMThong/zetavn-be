package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.model.entity.PostLikeEntity;

import java.util.List;
import java.util.stream.Collectors;

public class PostLikeMapper {
    public static UserMentionDto entityToUserDto(PostLikeEntity entity) {
        UserMentionDto dto = new UserMentionDto();
        dto.setId(entity.getUserEntity().getUserId());
        dto.setUsername(entity.getUserEntity().getUsername());
        dto.setFirstName(entity.getUserEntity().getFirstName());
        dto.setLastName(entity.getUserEntity().getLastName());
        dto.setDisplay(entity.getUserEntity().getFirstName() + " " + entity.getUserEntity().getLastName());
        dto.setAvatar(entity.getUserEntity().getAvatar());
        dto.setPoster(entity.getUserEntity().getPoster());
        return dto;
    }

    public static List<UserMentionDto> entityListToUserDtoList(List<PostLikeEntity> postLikeEntities) {
        return postLikeEntities.stream()
                .map(PostLikeMapper::entityToUserDto)
                .collect(Collectors.toList());
    }
}
