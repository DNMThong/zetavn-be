package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.zetavn.api.enums.FriendStatusEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class FriendshipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private long friendshipId;

    @ManyToOne
    @JoinColumn(name = "sender_user_id", nullable = false)
    @JsonBackReference
    private UserEntity senderUserEntity;

    @ManyToOne
    @JoinColumn(name = "receiver_user_id", nullable = false)
    @JsonBackReference
    private UserEntity receiverUserEntity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendStatusEnum status;
}
