package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.*;
import com.zetavn.api.model.entity.PostEntity;

import java.util.ArrayList;
import java.util.List;

public class PostMapper {
    public static PostDto entityToDto(PostEntity entity) {
        PostDto dto = new PostDto();
        dto.setPostId(entity.getPostId());
        dto.setUserEntity(UserMentionMapper.entityToDto(entity.getUserEntity()));
        dto.setContent(entity.getContent());
        dto.setAccessModifier(entity.getAccessModifier());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getPostActivityEntityList() != null) {
            PostActivityDto postActivityDto = PostActivityMapper.entityToDto(entity.getPostActivityEntityList().get(0));
            dto.setPostActivity(postActivityDto);
        }

        if (entity.getPostMediaEntityList() != null) {
            List<PostMediaDto> postMediaDtoList = PostMediaMapper.entityListToDtoList(entity.getPostMediaEntityList());
            dto.setPostMedias(postMediaDtoList);
        }

        if (entity.getPostMentionEntityList() != null) {
            List<PostMentionDto> postMentionDtoList = PostMentionMapper.entityListToDtoList(entity.getPostMentionEntityList());
            dto.setPostMentions(postMentionDtoList);
        }
        return dto;
    }

    public static List<PostDto> entityListToDtoList(List<PostEntity> entityList) {
        List<PostDto> dtoList = new ArrayList<>();
        for (PostEntity entity : entityList) {
            PostDto dto = entityToDto(entity);
            dtoList.add(dto);
        }
        return dtoList;
    }

}
