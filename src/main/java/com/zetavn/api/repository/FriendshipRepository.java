package com.zetavn.api.repository;

import com.zetavn.api.model.entity.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
}
