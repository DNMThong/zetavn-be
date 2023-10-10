package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.PostMediaDto;
import com.zetavn.api.model.entity.PostMediaEntity;

import java.util.List;
import java.util.stream.Collectors;

public class PostMediaMapper {
    public static PostMediaDto entityToDto(PostMediaEntity postMediaEntity) {
        PostMediaDto postMediaDto = new PostMediaDto();
        postMediaDto.setMediaPath(postMediaEntity.getMediaPath());
        postMediaDto.setMediaType(postMediaEntity.getMediaType());
        return postMediaDto;
    }

    public static List<PostMediaDto> entityListToDtoList(List<PostMediaEntity> postMedias) {
        return postMedias.stream()
                .map(PostMediaMapper::entityToDto)
                .collect(Collectors.toList());
    }
}
