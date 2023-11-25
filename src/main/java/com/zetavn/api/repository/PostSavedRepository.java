package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostSavedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PostSavedRepository extends JpaRepository<PostSavedEntity, Long> {
    @Query("SELECT COUNT(o) FROM PostSavedEntity o WHERE  DATE(o.savedTime) >=?1 AND DATE(o.savedTime) <=?2")
    Long countPostsSavedInDateRange(LocalDate startDate, LocalDate endDate);
}
