package com.zetavn.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.zetavn.api.enums.MessageStatusEnum;
import com.zetavn.api.enums.MessageTypeEnum;
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
@Table(name = "message")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageTypeEnum type;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageStatusEnum status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonBackReference
    private UserEntity senderUser;

    @ManyToOne
    @JoinColumn(name = "reciever_id", nullable = false)
    @JsonBackReference
    private UserEntity recieverUser;

    @OneToOne(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private MessageCallEntity messageCall;
}
