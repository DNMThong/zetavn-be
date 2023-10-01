package com.zetavn.api.entity;

import com.zetavn.api.entity.enums.FollowPriority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Follows {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follows_id")
    private long followsId;

    @ManyToOne
    @JoinColumn(name = "follower_user_id", nullable = false)
    private Users followerUser;

    @ManyToOne
    @JoinColumn(name = "following_user_id", nullable = false)
    private Users followingUser;

    @Column(name = "priority", nullable = false)
    private FollowPriority priority;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
