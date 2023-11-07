package com.zetavn.api.service;

import com.zetavn.api.model.entity.UserInfoEntity;
import com.zetavn.api.payload.request.UserInfoRequest;
import com.zetavn.api.payload.response.ApiResponse;

public interface UserInfoService {

    ApiResponse<?> create(UserInfoRequest request);

    ApiResponse<?> update(String userId, UserInfoRequest request);

    ApiResponse<?> remove();

    ApiResponse<?> getUserInfoByUsername(String username);

}
