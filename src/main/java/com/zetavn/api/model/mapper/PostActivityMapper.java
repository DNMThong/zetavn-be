package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.PostActivityDto;
import com.zetavn.api.model.entity.PostActivityEntity;

public class PostActivityMapper {
    public static PostActivityDto entityToDto(PostActivityEntity entity) {
        PostActivityDto dto = new PostActivityDto();
        dto.setId(entity.getDetail().getId());
        dto.setTitle(entity.getDetail().getTitle());
        dto.setName(entity.getDetail().getName());
        dto.setDesc(entity.getDetail().getDesc());
        dto.setPic(entity.getDetail().getPic());
        dto.setDetail(ActivityStatusMapper.entityToDetailDto(entity));
        return dto;
    }
}
