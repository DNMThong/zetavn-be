package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.zetavn.api.enums.UserStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "phone")
    private String phone;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "poster")
    private String poster;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "is_authorized")
    private Boolean isAuthorized;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "token")
    private String token;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<ActivityLog> userActivitiesList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<Comment> userCommentList;

    @OneToMany(mappedBy = "followerUser", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<Follow> userFollowerList;

    @OneToMany(mappedBy = "followingUser", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<Follow> userFollowingList;

    @OneToMany(mappedBy = "senderUser", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<Friendship> userSenderList;

    @OneToMany(mappedBy = "receiverUser", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<Friendship> userReceiverList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostLike> userPostLikeList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostMedia> userPostMediaList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostMention> userPostMentionList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<UserInfo> userInfoList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<Post> userPostList;
}
