package com.zetavn.api.repository;

import com.zetavn.api.enums.TokenStatusEnum;
import com.zetavn.api.model.entity.ComfirmationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComfirmationTokenRepository extends JpaRepository<ComfirmationTokenEntity,Long> {

    @Query("select c from ComfirmationTokenEntity c where c.token like :token and c.status = :status")
    ComfirmationTokenEntity getComfirmationToken(@Param("token") String token,@Param("status") TokenStatusEnum status);
}
