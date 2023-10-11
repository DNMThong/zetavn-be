package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.CategoriesDto;
import com.zetavn.api.model.dto.PostActivityDto;
import com.zetavn.api.model.entity.PostActivityEntity;

public class PostActivityMapper {
    public static PostActivityDto entityToDto(PostActivityEntity entity) {
        PostActivityDto dto = new PostActivityDto();
        dto.setPostActivityId(entity.getPostActivityId());
        CategoriesDto categoriesDto = new CategoriesDto();
        categoriesDto.setCategoryId(entity.getCategoryEntity().getCategoryId());
        categoriesDto.setTitle(entity.getCategoryEntity().getTitle());
        categoriesDto.setName(entity.getCategoryEntity().getName());
        categoriesDto.setDescription(entity.getCategoryEntity().getDescription());
        categoriesDto.setIcon(entity.getCategoryEntity().getIcon());
        categoriesDto.setCategoryParent(entity.getCategoryEntity().getCategoryParent());
        dto.setCategoryEntity(categoriesDto);
        return dto;
    }
}
