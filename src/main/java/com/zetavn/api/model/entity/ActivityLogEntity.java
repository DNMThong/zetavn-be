package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "activity_log")
public class ActivityLogEntity {
    @Id
    @Column(name = "activity_log_id")
    private String activityLogId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private UserEntity userEntity;

    @Column(name = "online_time", nullable = false)
    private LocalDateTime onlineTime;

    @Column(name = "offline_time")
    private LocalDateTime offlineTime;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "device_information")
    private String deviceInformation;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
