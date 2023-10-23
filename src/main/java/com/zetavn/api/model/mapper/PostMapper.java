package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.*;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {
    public static PostDto entityToDto(PostEntity entity) {
        PostDto dto = new PostDto();
        dto.setId(entity.getPostId());
        dto.setUser(UserMentionMapper.entityToDto(entity.getUserEntity()));
        dto.setContent(entity.getContent());
        dto.setAccessModifier(entity.getAccessModifier());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdateAt(entity.getUpdatedAt());

        if (entity.getPostActivityEntity() != null) {
            PostActivityDto postActivityDto = PostActivityMapper.entityToDto(entity.getPostActivityEntity());
            dto.setActivity(postActivityDto);
        }

        if (entity.getPostMediaEntityList() != null) {
            List<PostMediaDto> postMediaDtoList = PostMediaMapper.entityListToDtoList(entity.getPostMediaEntityList());
            dto.setMedias(postMediaDtoList);
        }

        if (entity.getPostMentionEntityList() != null) {
            List<UserEntity> users = entity.getPostMentionEntityList().stream().map(item -> item.getUserEntity()).collect(Collectors.toList());
            dto.setMentions(UserMentionMapper.entityListToDtoList(users));
        }

        if (entity.getPostLikeEntityList() != null) {
            dto.setCountLike(entity.getPostLikeEntityList().size());
            List<UserMentionDto> userMentionDtoList = PostLikeMapper.entityListToUserDtoList(entity.getPostLikeEntityList());
            dto.setUsersLike(userMentionDtoList.subList(0, Math.min(userMentionDtoList.size(), 4)));
        } else {
            dto.setCountLike(0);
            dto.setUsersLike(null);
        }

        if (entity.getCommentEntityList() != null) {
            dto.setCountComment(entity.getCommentEntityList().size());
        } else {
            dto.setCountComment(0);
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
