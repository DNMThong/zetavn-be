package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.zetavn.api.enums.PostAccessModifierEnum;
import com.zetavn.api.enums.PostStatusEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Posts")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private String postId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private UserEntity userEntity;

    @Column(name = "content")
    private String content;

    @Column(name = "access_modifier")
    @Enumerated(EnumType.STRING)
    private PostAccessModifierEnum accessModifier;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PostStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "post_activity_detail_id")
    @JsonBackReference
    private PostActivityEntity postActivityEntityDetailId;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<CommentEntity> postCommentListEntity;

    @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostLikeEntity> postLikeEntityList;

    @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostMediaEntity> postMediaEntityList;

    @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostMentionEntity> postMentionEntityList;

    @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostSavedEntity> postSavedEntityList;
}
