package com.zetavn.api.model.mapper;

import com.zetavn.api.model.entity.UserInfoEntity;
import com.zetavn.api.payload.response.UserInfoResponse;

public  class  UserInfoMapper {
    public static UserInfoResponse userInfoEntityToUserInfoResponse(UserInfoEntity userInfo) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setGenderEnum(userInfo.getGenderEnum());
        userInfoResponse.setBirthday(userInfo.getBirthday());
        userInfoResponse.setGenderEnum(userInfo.getGenderEnum());
        userInfoResponse.setLivesAt(userInfo.getLivesAt());
        userInfoResponse.setStudiedAt(userInfo.getStudiedAt());
        userInfoResponse.setWorksAt(userInfo.getWorksAt());
        userInfoResponse.setAboutMe(userInfo.getAboutMe());
        userInfoResponse.setUpdateAt(userInfo.getUpdatedAt());
        return userInfoResponse;
    }
}
