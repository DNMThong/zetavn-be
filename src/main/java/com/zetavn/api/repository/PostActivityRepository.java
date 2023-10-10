package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostActivityRepository extends JpaRepository<PostActivityEntity, Long> {
}
