package com.zetavn.api.service.impl;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.model.mapper.UserMentionMapper;
import com.zetavn.api.repository.PostMentionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostMentionServiceImpl {
    @Autowired
    private PostMentionRepository postMentionRepository;

    List<UserMentionDto> getAllUserDtoByPostId(String postId) {
        return UserMentionMapper.entityListToDtoList(postMentionRepository.getAllUserByPostId(postId));
    }
}
