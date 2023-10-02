package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.zetavn.api.model.entity.enums.PostAccessModifier;
import com.zetavn.api.model.entity.enums.PostStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private long postId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private Users user;

    @Column(name = "slug", nullable = false, length = 36)
    private String slug;

    @Column(name = "content")
    private String content;

    @Column(name = "access_modifier")
    @Enumerated(EnumType.STRING)
    private PostAccessModifier accessModifier;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @ManyToOne
    @JoinColumn(name = "post_activity_detail_id")
    @JsonBackReference
    private PostActivity postActivityDetailId;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    List<Comment> postCommentList;

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    List<PostLike> postLikeList;

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    List<PostMedia> postMediaList;

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    List<PostMention> postMentionList;

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    List<PostSaved> postSavedList;
}
