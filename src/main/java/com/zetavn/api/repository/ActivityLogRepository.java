package com.zetavn.api.repository;

import com.zetavn.api.model.entity.ActivityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, String> {
    @Query("select CASE WHEN COUNT(a) > 0 THEN true ELSE false END from ActivityLogEntity a where a.userEntity.userId = :userId and a.offlineTime is null and a.onlineTime <= CURRENT_TIMESTAMP()")
    boolean checkUserOnline(@Param("userId") String userId);
}
