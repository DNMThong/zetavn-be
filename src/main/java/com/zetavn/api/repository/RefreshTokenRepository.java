package com.zetavn.api.repository;

import com.zetavn.api.model.entity.RefreshTokenEntity;
import jakarta.transaction.Transactional;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Order(1)
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    RefreshTokenEntity findRefreshTokenEntityByToken(String token);

    @Transactional
    void deleteRefreshTokenEntityByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.ipAddress LIKE :ip AND rt.userEntity.userId LIKE :userId")
    void deleteRefreshTokenEntityByIpAddressAndUserId(String ip, String userId);

}
