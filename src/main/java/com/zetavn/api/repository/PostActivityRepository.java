package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostActivityRepository extends JpaRepository<PostActivityEntity, Long> {
}
