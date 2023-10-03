package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostMentionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostMentionRepository extends JpaRepository<PostMentionEntity, Long> {
}
