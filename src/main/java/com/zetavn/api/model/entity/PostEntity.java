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

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostActivityEntity> postActivityEntityList;

    @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostMediaEntity> postMediaEntityList;

    @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostMentionEntity> postMentionEntityList;
}
