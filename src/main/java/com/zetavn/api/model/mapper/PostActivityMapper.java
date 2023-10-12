package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.PostActivityDto;
import com.zetavn.api.model.entity.PostActivityEntity;

public class PostActivityMapper {
    public static PostActivityDto entityToDto(PostActivityEntity entity) {
        PostActivityDto dto = new PostActivityDto();
        dto.setPostActivityId(entity.getPostActivityId());
        dto.setTitle(entity.getTitle());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setIcon(entity.getIcon());
        dto.setPostActivityEntityParent(entity.getPostActivityEntityParent());
        return dto;
    }
}
