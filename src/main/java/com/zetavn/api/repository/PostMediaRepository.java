package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMediaEntity, Long> {
}
