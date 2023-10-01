package com.zetavn.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_log_id")
    private long activityLogId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "online_time", nullable = false)
    private LocalDateTime onlineTime;

    @Column(name = "offline_time", nullable = false)
    private LocalDateTime offlineTime;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "device_information", nullable = false)
    private String deviceInformation;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
