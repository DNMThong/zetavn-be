package com.zetavn.api.model.entity;

import com.zetavn.api.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "message_call")
public class MessageCallEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_call_id")
    private Long messageCallId;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CallTypeEnum type;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageCallStatusEnum status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private MessageEntity message;
}


