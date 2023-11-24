package com.zetavn.api.repository;

import com.zetavn.api.model.entity.MessageEntity;
import com.zetavn.api.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<MessageEntity,Long> {

    @Query("select m from MessageEntity m where (m.senderUser.userId = :userId1 and m.recieverUser.userId = :userId2) " +
            "or (m.senderUser.userId = :userId2 and m.recieverUser.userId = :userId1) order by m.createdAt desc")
    Page<MessageEntity> getChatMessagePagination(String userId1, String userId2, Pageable pageable);

}
