package com.zetavn.api.repository;

import com.zetavn.api.model.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Posts, Long> {
}
