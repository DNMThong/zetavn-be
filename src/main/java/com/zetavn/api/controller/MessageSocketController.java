package com.zetavn.api.controller;

import com.zetavn.api.payload.request.MessageRequest;
import com.zetavn.api.payload.response.MessageResponse;
import com.zetavn.api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MessageSocketController {
    private final SimpMessagingTemplate template;

    private final MessageService messageService;

    @Autowired
    public MessageSocketController(SimpMessagingTemplate template, MessageService messageService) {
        this.template = template;
        this.messageService = messageService;
    }

    @MessageMapping("/message")
    public void createChatMessages(MessageRequest messageRequest) {
        MessageResponse message = messageService.createMessage(messageRequest);
        if(message!=null) {
            template.convertAndSendToUser(message.getSender().getId(),"/topic/message",message);
            template.convertAndSendToUser(message.getReciever().getId(),"/topic/message",message);
        }
    }
}
