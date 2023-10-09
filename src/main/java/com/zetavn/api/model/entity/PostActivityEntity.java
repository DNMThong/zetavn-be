package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "post_activity")
public class PostActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_activity_id")
    private long postActivityId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private PostEntity postEntity;

    @Column(name = "activity")
    private String activity;

    @Column(name = "description")
    private String description;

    @Column(name = "activity_icon_path")
    private String activityIconPath;

    @Column(name = "post_activity_parent_id")
    private Long postActivityParentId;
}
