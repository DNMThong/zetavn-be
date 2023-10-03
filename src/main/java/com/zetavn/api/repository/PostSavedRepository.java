package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostSavedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostSavedRepository extends JpaRepository<PostSavedEntity, Long> {
}
