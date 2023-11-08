package com.zetavn.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
//@EnableWebSocketSecurity
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
//    @Bean
//    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
//        messages.simpTypeMatchers(SimpMessageType.CONNECT,
//                        SimpMessageType.HEARTBEAT,
//                        SimpMessageType.UNSUBSCRIBE,
//                        SimpMessageType.DISCONNECT)
//                .permitAll()
//                .anyMessage().authenticated()
//                .simpDestMatchers("/**").authenticated();
//
//        return messages.build();
//    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .anyMessage().permitAll()
                .simpTypeMatchers(SimpMessageType.CONNECT).authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

}
