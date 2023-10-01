package com.zetavn.api.entity;

import com.zetavn.api.entity.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private long friendshipId;

    @ManyToOne
    @JoinColumn(name = "sender_user_id", nullable = false)
    private Users senderUser;

    @ManyToOne
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private Users receiverUser;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    private FriendStatus status;
}
