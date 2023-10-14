package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostSavedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostSavedRepository extends JpaRepository<PostSavedEntity, Long> {
}
