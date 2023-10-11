package com.zetavn.api.repository;

import com.zetavn.api.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findUserEntityByEmail(String email);
    UserEntity findUserEntityByUsername(String username);

    @Query("SELECT o FROM UserEntity o WHERE o.username LIKE ?1 OR o.email LIKE ?2")
    UserEntity findUserEntityByUsernameAndEmail(String username, String email);
}
