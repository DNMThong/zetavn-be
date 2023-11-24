package com.zetavn.api.model.mapper;

import com.zetavn.api.model.entity.MessageEntity;
import com.zetavn.api.payload.response.MessageResponse;
import com.zetavn.api.repository.ActivityLogRepository;
import com.zetavn.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


public class MessageMapper {



    public static MessageResponse entityToDto(MessageEntity message) {
        MessageResponse messageDto = new MessageResponse();
        messageDto.setId(message.getMessageId());
        messageDto.setMessage(message.getMessage());
        messageDto.setType(message.getType());
        messageDto.setStatus(message.getStatus());
        messageDto.setReciever(OverallUserMapper.entityToDto(message.getRecieverUser()));
        messageDto.setSender(OverallUserMapper.entityToDto(message.getSenderUser()));
        messageDto.setCreatedAt(message.getCreatedAt());

        return messageDto;
    }
}

//    public MessageResponse entityToDto(MessageEntity message) {
//        MessageResponse messageDto = new MessageResponse();
//        messageDto.setId(message.getMessageId());
//        messageDto.setMessage(message.getMessage());
//        messageDto.setType(message.getType());
//        messageDto.setStatus(message.getStatus());
//
//        // check user reciever online
//        OverallUserPrivateResponse reciever = OverallUserMapper.entityToOverallUserPrivate(message.getRecieverUser());
//        reciever.setIsOnline(activityLogRepository.checkUserOnline(reciever.getId()));
//        messageDto.setReciever(reciever);
//
//        // check user sender online
//        OverallUserPrivateResponse sender = OverallUserMapper.entityToOverallUserPrivate(message.getSenderUser());
//        sender.setIsOnline(activityLogRepository.checkUserOnline(sender.getId()));
//        messageDto.setSender(reciever);
//
//        messageDto.setCreatedAt(message.getCreatedAt());
//
//
//        return messageDto;
//    }