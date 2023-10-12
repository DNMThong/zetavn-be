package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostActivityRepository extends JpaRepository<PostActivityEntity, Long> {
    @Query("SELECT o FROM PostActivityEntity o WHERE o.postActivityId = ?1 AND o.postActivityEntityParent.postActivityId IS NOT NULL")
    PostActivityEntity getPostActivityById(int categoryId);
}
