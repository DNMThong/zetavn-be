package com.zetavn.api.service.impl;

import com.zetavn.api.service.ZegocloudService;
import com.zetavn.api.utils.TokenServerAssistant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ZegocloudServiceImpl implements ZegocloudService {

    @Value("${zetavn.zego.app-id}")
    Long appId;

    @Value("${zetavn.zego.server-secret}")
    String serverSecret;

    Integer effectiveTimeInSeconds = 3600;

    @Override
    public String generateToken(String userId) {
        TokenServerAssistant.VERBOSE = false;
        TokenServerAssistant.TokenInfo token = TokenServerAssistant.generateToken04(appId,  userId, serverSecret, effectiveTimeInSeconds,"");
        return token.data;
    }
}
