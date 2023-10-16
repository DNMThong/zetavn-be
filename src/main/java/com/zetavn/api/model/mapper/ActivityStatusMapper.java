package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.ActivityStatus;
import com.zetavn.api.model.dto.ActivityStatusDetail;
import com.zetavn.api.model.entity.PostActivityEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ActivityStatusMapper {
    public static ActivityStatus entityToDto(PostActivityEntity entity, List<ActivityStatusDetail> detailList) {
        ActivityStatus dto = new ActivityStatus();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setName(entity.getName());
        dto.setDesc(entity.getDesc());
        dto.setPic(entity.getPic());
        dto.setDetails(detailList);
        return dto;
    }

    public static ActivityStatusDetail entityToDetailDto(PostActivityEntity entity) {
        ActivityStatusDetail dto = new ActivityStatusDetail();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDesc(entity.getDesc());
        dto.setPic(entity.getPic());
        return dto;
    }

    public static List<ActivityStatusDetail> entityListToDetailDtoList(List<PostActivityEntity> postActivities) {
        return postActivities.stream()
                .map(ActivityStatusMapper::entityToDetailDto)
                .collect(Collectors.toList());
    }
}
