package com.zetavn.api.socket;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class WebSocketEventListener {

//    @EventListener
//    private void handleSessionConnected(SessionConnectEvent event) throws Exception {
//        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
//        String username = headers.getUser().getName();
//
//
//        log.info("Connect socket: {}",event.getUser().getName());
//    }
//
//    @EventListener
//    private void handleSessionDisconnect(SessionDisconnectEvent event) throws Exception {
//        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
//        String username = headers.getUser().getName();
//
//        log.info("Disconnect socket: {}",username);
//    }
}
