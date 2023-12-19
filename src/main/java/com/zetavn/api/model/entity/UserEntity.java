package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.zetavn.api.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.zetavn.api.enums.UserStatusEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Users")
public class UserEntity implements UserDetails {
    @Id
    @Column(name = "user_id")
    private String userId;

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
    private UserStatusEnum status;

    @Column(name = "is_authorized")
    private Boolean isAuthorized;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "token")
    private String token;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "followingUserEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<FollowEntity> userFollowingList;

    @OneToMany(mappedBy = "senderUserEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<FriendshipEntity> userSenderList;

    @OneToMany(mappedBy = "receiverUserEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<FriendshipEntity> userReceiverList;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostLikeEntity> userPostLikeEntityList;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostMediaEntity> userPostMediaEntityList;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostMentionEntity> userPostMentionEntityList;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostEntity> userPostListEntity;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<RefreshTokenEntity> refreshTokenEntityList;

    @OneToOne(mappedBy = "userEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    UserInfoEntity userInfo;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<ComfirmationTokenEntity> comfirmationTokenEntities;

    @OneToMany(mappedBy = "senderUser", fetch = FetchType.LAZY)
    @JsonManagedReference
    @OrderBy("createdAt asc")
    List<MessageEntity> senderMessages;

    @OneToMany(mappedBy = "recieverUser", fetch = FetchType.LAZY)
    @JsonManagedReference
    @OrderBy("createdAt asc")
    List<MessageEntity> recieverMessages;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    @Override
    public String getPassword() {
        return password ;
    }
    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatusEnum.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
