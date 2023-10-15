package com.zetavn.api.service.impl;

import com.zetavn.api.model.dto.ActivityStatus;
import com.zetavn.api.model.entity.PostActivityEntity;
import com.zetavn.api.model.mapper.ActivityStatusMapper;
import com.zetavn.api.model.mapper.PostActivityMapper;
import com.zetavn.api.repository.PostActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostActivityServiceImpl {
    @Autowired
    private PostActivityRepository postActivityRepository;

    public List<ActivityStatus> getAll() {
        List<ActivityStatus> list = new ArrayList<>();
        List<PostActivityEntity> postActivityList = postActivityRepository.getAllActivity();
        for (PostActivityEntity postActivity : postActivityList) {
            List<PostActivityEntity> activityParentList = postActivityRepository.getAllActivityParent(postActivity.getId());
            ActivityStatus dto = ActivityStatusMapper.entityToDto(postActivity, ActivityStatusMapper.entityListToDetailDtoList(activityParentList));
            list.add(dto);
        }
        return list;
    }
}
