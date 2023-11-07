package com.zetavn.api.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.socket.UserHandshakeHandler;
import com.zetavn.api.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;

import java.util.Date;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${zetavn.domain}")
    private String domain;

    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(domain)
                .setHandshakeHandler(new UserHandshakeHandler(jwtHelper,userRepository))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app")
                .setUserDestinationPrefix("/user")
                .enableSimpleBroker("/topic","/queue","/user");
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
//                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//                        String token = authorizationHeader.substring("Bearer ".length());
//                        DecodedJWT decodedJWT = jwtHelper.decodedJWT(token);
//                        if(decodedJWT.getExpiresAt().after(new Date())) {
//                            String username = decodedJWT.getSubject();
//                            UserEntity user = userRepository.findUserEntityByEmail(username);
//                            if(user!=null) {
//                                accessor.setUser(() -> user.getUserId());
//                            }
//                        }
//                    }
//                }
//                return message;
//            }
//        });
//    }

}
