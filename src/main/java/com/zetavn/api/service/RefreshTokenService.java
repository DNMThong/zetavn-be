package com.zetavn.api.service;

import com.zetavn.api.model.entity.RefreshTokenEntity;

public interface RefreshTokenService {

    RefreshTokenEntity create(String token, String userId, String ipAddress);

    RefreshTokenEntity remove(Long id);

    RefreshTokenEntity getRefreshTokenByToken(String token);

    void removeRefreshTokenByToken(String token);

    void removeRefreshTokenByIpAddressAndUserId(String ip, String userId);

}
