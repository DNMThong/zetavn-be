package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {
}
