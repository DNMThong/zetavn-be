package com.zetavn.api.model.mapper;

import com.zetavn.api.model.entity.MessageCallEntity;
import com.zetavn.api.model.entity.MessageEntity;
import com.zetavn.api.payload.response.MessageCallResponse;
import com.zetavn.api.payload.response.MessageResponse;

public class MessageCallMapper {
    public static MessageCallResponse entityToDto(MessageCallEntity messageCall) {
        MessageCallResponse messageCallResponse = new MessageCallResponse();
        messageCallResponse.setDuration(messageCall.getDuration());
        messageCallResponse.setType(messageCall.getType());
        messageCallResponse.setStatus(messageCall.getStatus());

        return messageCallResponse;
    }
}
