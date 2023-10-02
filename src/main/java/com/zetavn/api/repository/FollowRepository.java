package com.zetavn.api.repository;

import com.zetavn.api.model.entity.Follows;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follows, Long> {
}
