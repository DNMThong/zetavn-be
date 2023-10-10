package com.zetavn.api.service.impl;

import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl {
    @Autowired
    private UserRepository userRepository;

    public UserEntity getUserByUserId(String userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        return userOptional.get();
    }
}
