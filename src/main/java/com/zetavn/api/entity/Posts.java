package com.zetavn.api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.zetavn.api.entity.enums.PostAccessModifier;
import com.zetavn.api.entity.enums.PostStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "post_key", nullable = false, length = 36)
    private String postKey;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "content")
    private String content;

    @Column(name = "access_modifier")
    private PostAccessModifier accessModifier;

    @Column(name = "status")
    private PostStatus status;

    @ManyToOne
    @JoinColumn(name = "post_activity_detail_id")
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
