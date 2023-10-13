package com.zetavn.api.model.entity;

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

    @Column(name = "title")
    private String title;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "icon")
    private String icon;

    @Column(name = "post_activity_parent_id")
    private Long postActivityParentId;

    @OneToMany(mappedBy = "postActivityEntityDetailId", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<PostEntity> postEntityActivityDetailList;
}
