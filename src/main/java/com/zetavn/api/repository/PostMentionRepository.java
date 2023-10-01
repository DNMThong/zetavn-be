package com.zetavn.api.repository;

import com.zetavn.api.entity.PostMention;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostMentionRepository extends JpaRepository<PostMention, Long> {
}
