package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostActivityRepository extends JpaRepository<PostActivityEntity, Integer> {
    @Query("SELECT o FROM PostActivityEntity o WHERE o.id = ?1 AND o.detail.id IS NOT NULL")
    PostActivityEntity getActivityById(int categoryId);

    @Query("SELECT o FROM PostActivityEntity o WHERE o.detail.id IS NULL")
    List<PostActivityEntity> getAllActivity();

    @Query("SELECT o FROM PostActivityEntity o WHERE o.detail.id = ?1")
    List<PostActivityEntity> getAllActivityParent(int categoryId);
}
