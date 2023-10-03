package com.zetavn.api.repository;

import com.zetavn.api.model.entity.ActivityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, Long> {
}
