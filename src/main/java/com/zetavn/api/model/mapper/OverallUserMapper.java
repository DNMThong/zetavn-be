package com.zetavn.api.model.mapper;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.response.OverallUserPrivateResponse;
import com.zetavn.api.payload.response.OverallUserResponse;
import com.zetavn.api.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OverallUserMapper {
    private ActivityLogRepository activityLogRepository;

    @Autowired
    public OverallUserMapper(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public static OverallUserResponse entityToDto(UserEntity userEntity) {
        OverallUserResponse userMentionDto = new OverallUserResponse();
        userMentionDto.setId(userEntity.getUserId());
        userMentionDto.setUsername(userEntity.getUsername());
        userMentionDto.setDisplay(userEntity.getFirstName()+" "+userEntity.getLastName());
        userMentionDto.setFirstName(userEntity.getFirstName());
        userMentionDto.setLastName(userEntity.getLastName());
        userMentionDto.setAvatar(userEntity.getAvatar());
        userMentionDto.setPoster(userEntity.getPoster());
        return userMentionDto;
    }

    public static OverallUserPrivateResponse entityToOverallUserPrivate(UserEntity userEntity) {
        OverallUserPrivateResponse userResponse = new OverallUserPrivateResponse();
        userResponse.setId(userEntity.getUserId());
        userResponse.setUsername(userEntity.getUsername());
        userResponse.setDisplay(userEntity.getFirstName()+" "+userEntity.getLastName());
        userResponse.setFirstName(userEntity.getFirstName());
        userResponse.setLastName(userEntity.getLastName());
        userResponse.setAvatar(userEntity.getAvatar());
        userResponse.setPoster(userEntity.getPoster());


        return userResponse;
    }
}
