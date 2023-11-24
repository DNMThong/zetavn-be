package com.zetavn.api.payload.request;

import com.zetavn.api.enums.MessageTypeEnum;
import com.zetavn.api.payload.response.OverallUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String senderId;
    private String recieverId;
    private String message;
    private MessageTypeEnum type;
}
