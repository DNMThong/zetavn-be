package com.zetavn.api.repository;

import com.zetavn.api.model.entity.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
}
