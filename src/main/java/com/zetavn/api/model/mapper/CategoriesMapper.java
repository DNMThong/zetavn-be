package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.CategoriesDto;
import com.zetavn.api.model.entity.CategoryEntity;

public class CategoriesMapper {
    public static CategoriesDto entityToDto(CategoryEntity entity) {
        CategoriesDto dto = new CategoriesDto();
        dto.setCategoryId(entity.getCategoryId());
        dto.setTitle(entity.getTitle());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setIcon(entity.getIcon());
        dto.setCategoryParent(entity.getCategoryParent());
        return dto;
    }
}
