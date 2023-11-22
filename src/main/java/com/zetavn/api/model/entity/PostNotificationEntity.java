package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.zetavn.api.enums.FollowPriorityEnum;
import com.zetavn.api.enums.PostNotificationEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "post_notification")
public class PostNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference
    private PostEntity post;

    @ManyToOne
    @JoinColumn(name = "interacting_user_id", nullable = false)
    @JsonBackReference
    private UserEntity interactingUser;

    @ManyToOne
    @JoinColumn(name = "receiving_user_id", nullable = false)
    @JsonBackReference
    private UserEntity receivingUser;

    @Column(name = "related_id", nullable = false)
    private long relatedId;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostNotificationEnum type;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;
}

