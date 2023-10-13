package com.zetavn.api.service.impl;

import com.zetavn.api.model.entity.RefreshTokenEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.repository.RefreshTokenRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RefreshTokenImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public RefreshTokenEntity create(String token, String userId, String ipAddress) {
        log.info("TOKEN BEFORE STORE: {}", token);
        UserEntity user = userRepository.findById(userId).get();
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setToken(token);
        refreshTokenEntity.setUserEntity(user);
        refreshTokenEntity.setIpAddress(ipAddress);
        return refreshTokenRepository.save(refreshTokenEntity);
    }


    @Override
    public RefreshTokenEntity remove(Long id) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findById(id).get();
        refreshTokenRepository.deleteById(id);

        return refreshTokenEntity;
    }

    @Override
    public RefreshTokenEntity getRefreshTokenByToken(String token) {
        return refreshTokenRepository.findRefreshTokenEntityByToken(token);
    }

    @Override
    public void removeRefreshTokenByToken(String token) {
        refreshTokenRepository.deleteRefreshTokenEntityByToken(token);
    }

    @Override
    public void removeRefreshTokenByIpAddressAndUserId(String ip, String userId) {
        refreshTokenRepository.deleteRefreshTokenEntityByIpAddressAndUserId(ip, userId);
    }
}
