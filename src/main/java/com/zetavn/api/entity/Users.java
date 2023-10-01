package com.zetavn.api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.zetavn.api.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "user_key", nullable = false, unique = true)
    private String userKey;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", unique = true)
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

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    List<ActivityLog> userActivitiesList;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    List<Comment> userCommentList;

    @OneToMany(mappedBy = "followerUser")
    @JsonManagedReference
    List<Follows> userFollowerList;

    @OneToMany(mappedBy = "followingUser")
    @JsonManagedReference
    List<Follows> userFollowingList;

    @OneToMany(mappedBy = "senderUser")
    @JsonManagedReference
    List<Friendship> userSenderList;

    @OneToMany(mappedBy = "receiverUser")
    @JsonManagedReference
    List<Friendship> userReceiverList;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    List<PostLike> userPostLikeList;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    List<PostMedia> userPostMediaList;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    List<PostMention> userPostMentionList;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    List<UserInfo> userInfoList;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    List<Posts> userPostList;
}
