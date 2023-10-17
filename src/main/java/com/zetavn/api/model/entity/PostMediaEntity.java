package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "post_media")
public class PostMediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_media_id")
    private long postMediaId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = true)
    @JsonBackReference
    private PostEntity postEntity;

    @Column(name = "media_path", nullable = false)
    private String mediaPath;

    @Column(name = "media_type", nullable = false, length = 100)
    private String mediaType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private UserEntity userEntity;
}
