package com.zetavn.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class NotificationController {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/hello")
    public void send(@Payload String name, Principal principal) {

//        simpMessagingTemplate.convertAndSendToUser(principal.getName(),"/topic/hello","hello");
//        String destination = "/topic/hello";
//        simpMessagingTemplate.convertAndSendToUser(name, destination, "Hello, User!");
        if(principal!=null) {
            System.out.println(principal.getName());
        }

    }

}
