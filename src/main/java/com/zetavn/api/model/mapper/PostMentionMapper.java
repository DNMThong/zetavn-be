package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.PostMentionDto;
import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.model.entity.PostMentionEntity;

import java.util.ArrayList;
import java.util.List;

public class PostMentionMapper {
    public static PostMentionDto entityToDto(PostMentionEntity entity) {
        PostMentionDto dto = new PostMentionDto();
        dto.setId(entity.getMentionId());
        UserMentionDto userMentionDto = UserMentionMapper.entityToDto(entity.getUserEntity());
        dto.setUser(userMentionDto);
        return dto;
    }

    public static List<PostMentionDto> entityListToDtoList(List<PostMentionEntity> entityList) {
        List<PostMentionDto> dtoList = new ArrayList<>();
        for (PostMentionEntity entity : entityList) {
            PostMentionDto dto = entityToDto(entity);
            dtoList.add(dto);
        }
        return dtoList;
    }

}
