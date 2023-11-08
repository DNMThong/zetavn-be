package com.zetavn.api.socket;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sun.security.auth.UserPrincipal;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.utils.JwtHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class UserHandshakeHandler extends DefaultHandshakeHandler {
    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;



    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
//        String authorizationHeader = request.getHeaders().getFirst("Authorization");
        String token = request.getURI().getQuery().replace("token=", "");
        log.info("Token {}",token);
        if (token != null) {
            DecodedJWT decodedJWT = jwtHelper.decodedJWT(token);
            if(decodedJWT.getExpiresAt().after(new Date())) {
                String username = decodedJWT.getSubject();
                UserEntity user = userRepository.findUserEntityByEmail(username);
                if(user!=null) {
                    log.info("User id: {}",user.getUserId());
                    return new UserPrincipal(user.getUserId());
                }
            }
        }
        return null;
    }
}
