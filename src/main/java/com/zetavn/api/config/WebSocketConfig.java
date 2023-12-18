package com.zetavn.api.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.zetavn.api.jwt.JWTAuthenticationToken;
import com.zetavn.api.model.entity.ActivityLogEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.repository.ActivityLogRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.OnlineUserServiceImpl;
import com.zetavn.api.socket.UserHandshakeHandler;
import com.zetavn.api.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Arrays.stream;

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

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
//                .setHandshakeHandler(new UserHandshakeHandler(jwtHelper,userRepository))
                .setAllowedOrigins(domain);

        registry.addEndpoint("/ws")
                .setAllowedOrigins(domain)
//                .setHandshakeHandler(new UserHandshakeHandler(jwtHelper,userRepository))
                .withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app")
                .setUserDestinationPrefix("/user")
                .enableSimpleBroker("/topic","/queue","/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                String sessionId = (String) message.getHeaders().get("simpSessionId");


                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
                    String ipAddress = accessor.getFirstNativeHeader("ip_address");
                    String deviceInformation = accessor.getFirstNativeHeader("device_information");
                    String saveActivityLog = accessor.getFirstNativeHeader("saveActivityLog");

                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        String token = authorizationHeader.substring("Bearer ".length());
                        DecodedJWT decodedJWT = jwtHelper.decodedJWT(token);
                        if(decodedJWT.getExpiresAt().after(new Date())) {
                            String username = decodedJWT.getSubject();
                            UserEntity user = userRepository.findUserEntityByEmail(username);

//                            if(user!=null&&saveActivityLog.equals("true")) {
//                                ActivityLogEntity activityLogEntity = new ActivityLogEntity();
//                                activityLogEntity.setActivityLogId(sessionId);
//                                activityLogEntity.setUserEntity(user);
//                                activityLogEntity.setCreatedAt(LocalDateTime.now());
//                                activityLogEntity.setOnlineTime(LocalDateTime.now());
//                                activityLogEntity.setIpAddress(ipAddress);
//                                activityLogEntity.setDeviceInformation(deviceInformation);
//                                activityLogRepository.save(activityLogEntity);
//                            }

                            String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                            stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(user.getUserId(), null, authorities);
                            accessor.setUser(authenticationToken);
                        }
                    }
                }

//                if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
//                    Optional<ActivityLogEntity> optional = activityLogRepository.findById(sessionId);
//                    if(optional.isPresent()) {
//                        ActivityLogEntity activityLog = optional.get();
//                        activityLog.setOfflineTime(LocalDateTime.now());
//                        activityLog.setUpdatedAt(LocalDateTime.now());
//                        activityLogRepository.save(activityLog);
//                    }
//
//                }

                return message;
            }
        });
    }

}
