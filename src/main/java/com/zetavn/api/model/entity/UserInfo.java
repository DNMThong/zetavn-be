package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.zetavn.api.model.entity.enums.Gender;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_info_id")
    private long usersInfoId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private Users user;

    @Column(name = "about_me")
    private String aboutMe;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender genderEnum;

    @Column(name = "studied_at")
    private String studiedAt;

    @Column(name = "works_at")
    private String worksAt;

    @Column(name = "lives_at")
    private String livesAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
