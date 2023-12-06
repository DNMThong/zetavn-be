package com.zetavn.api.controller;


import com.zetavn.api.payload.request.CallRequest;
import com.zetavn.api.payload.response.CallResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CallController {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/outgoing-call")
    public void outgoingCall(@Payload CallRequest callRequest) {
        CallResponse callResponse = new CallResponse(callRequest);
        simpMessagingTemplate.convertAndSendToUser(callRequest.getTo(),"/topic/incoming-call",callResponse);
    }

    @MessageMapping("/reject-incoming-call")
    public void rejectIncomingCall(@Payload String from) {
        simpMessagingTemplate.convertAndSendToUser(from,"/topic/reject-call","");
    }

    @MessageMapping("/accept-incoming-call")
    public void acceptIncomingCall(@Payload String from) {
        simpMessagingTemplate.convertAndSendToUser(from,"/topic/accept-call","");
    }
}
