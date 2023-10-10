package com.zetavn.api.repository;

import com.zetavn.api.model.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    @Query("SELECT o FROM CategoryEntity o WHERE o.categoryId = ?1 AND o.categoryParent.categoryId IS NOT NULL")
    CategoryEntity getDetailCategoryById(int categoryId);
}
