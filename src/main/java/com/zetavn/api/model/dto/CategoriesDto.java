package com.zetavn.api.model.dto;

import com.zetavn.api.model.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesDto {
    private int categoryId;
    private String title;
    private String name;
    private String description;
    private String icon;
    private CategoryEntity categoryParent;
}
