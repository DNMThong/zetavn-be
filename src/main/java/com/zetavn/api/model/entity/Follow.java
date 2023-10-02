package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.zetavn.api.enums.FollowPriority;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follows_id")
    private long followsId;

    @ManyToOne
    @JoinColumn(name = "follower_user_id", nullable = false)
    @JsonBackReference
    private User followerUser;

    @ManyToOne
    @JoinColumn(name = "following_user_id", nullable = false)
    @JsonBackReference
    private User followingUser;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private FollowPriority priority;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
