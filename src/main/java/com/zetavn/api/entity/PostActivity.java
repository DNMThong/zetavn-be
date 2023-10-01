package com.zetavn.api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class PostActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_activity_id")
    private long postActivityId;

    @Column(name = "activity")
    private String activity;

    @Column(name = "description")
    private String description;

    @Column(name = "activity_icon_path")
    private String activityIconPath;

    @Column(name = "post_activity_parent_id")
    private Long postActivityParentId;

    @OneToMany(mappedBy = "postActivityDetailId")
    @JsonManagedReference
    List<Posts> postActivityDetailList;
}
