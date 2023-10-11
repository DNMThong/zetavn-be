package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostMentionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostMentionRepository extends JpaRepository<PostMentionEntity, Long> {
}
