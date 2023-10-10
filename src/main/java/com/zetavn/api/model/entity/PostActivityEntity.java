package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post_activity")
public class PostActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_activity_id")
    private long postActivityId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    @JsonBackReference
    private PostEntity postEntity;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private CategoryEntity categoryEntity;
}
