package com.zetavn.api.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zetavn.api.enums.MessageStatusEnum;
import com.zetavn.api.enums.MessageTypeEnum;
import com.zetavn.api.model.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageResponse {
    private Long id;
    private String message;
    private MessageTypeEnum type;
    private MessageStatusEnum status;
    @JsonFormat(pattern = "hh:mma dd/MM/yyyy")
    private LocalDateTime createdAt;
    private OverallUserResponse sender;
    private OverallUserResponse reciever;
}
