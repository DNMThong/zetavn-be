package com.zetavn.api.model.mapper;

import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.payload.response.OverallUserResponse;
import org.springframework.stereotype.Component;

@Component
public class OverallUserMapper {
    public static OverallUserResponse entityToOverallUser(UserEntity userEntity) {
        OverallUserResponse overallUser = new OverallUserResponse();
        overallUser.setId(userEntity.getUserId());
        overallUser.setUsername(userEntity.getUsername());
        overallUser.setFirstName(userEntity.getFirstName());
        overallUser.setLastName(userEntity.getLastName());
        overallUser.setDisplay(userEntity.getFirstName()+" "+userEntity.getLastName());
        overallUser.setAvatar(userEntity.getAvatar());
        overallUser.setPoster(userEntity.getPoster());
        return overallUser;
    }
}
