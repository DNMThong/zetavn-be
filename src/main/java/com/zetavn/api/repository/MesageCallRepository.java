package com.zetavn.api.repository;

import com.zetavn.api.model.entity.MessageCallEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesageCallRepository extends JpaRepository<MessageCallEntity,Long> {
}
