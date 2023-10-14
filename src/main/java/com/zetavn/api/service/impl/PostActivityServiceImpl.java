package com.zetavn.api.service.impl;

import com.zetavn.api.model.dto.PostActivityParentDto;
import com.zetavn.api.model.entity.PostActivityEntity;
import com.zetavn.api.repository.PostActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostActivityServiceImpl {
    @Autowired
    private PostActivityRepository postActivityRepository;

    public List<PostActivityParentDto> getAll() {
        List<PostActivityParentDto> list = new ArrayList<>();
        List<PostActivityEntity> postActivityList = postActivityRepository.getAllActivity();
        for (PostActivityEntity postActivity : postActivityList) {
            PostActivityParentDto dto = new PostActivityParentDto();
            dto.setPostActivity(postActivity);
            List<PostActivityEntity> activityParentList = postActivityRepository.getAllActivityParent(postActivity.getPostActivityId());
            dto.setPostActivityList(activityParentList);
            list.add(dto);
        }
        return list;
    }
}
