package com.zetavn.api.model.mapper;

import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.entity.UserInfoEntity;
import com.zetavn.api.payload.response.UserInfoResponse;
import com.zetavn.api.model.dto.UserAdminDto;
import com.zetavn.api.payload.response.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserResponse userEntityToUserResponse(UserEntity userEntity){

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userEntity.getUserId());
        userResponse.setIsAuthorized(userEntity.getIsAuthorized());
        userResponse.setLastName(userEntity.getLastName());
        userResponse.setFirstName(userEntity.getFirstName());
        userResponse.setDisplay(userEntity.getFirstName() + " " + userEntity.getLastName());
        userResponse.setPoster(userEntity.getPoster());
        userResponse.setCreatedAt(userEntity.getCreatedAt());
        userResponse.setAvatar(userEntity.getAvatar());
        userResponse.setPhone(userEntity.getPhone());
        userResponse.setEmail(userEntity.getEmail());
        userResponse.setAvatar(userEntity.getAvatar());
        userResponse.setUpdatedAt(userEntity.getUpdatedAt());
        userResponse.setUsername(userEntity.getUsername());
        return  userResponse;
    }

    public static UserEntity userResponseToUserEntity(UserResponse userResponse) {
        UserEntity user = new UserEntity();
        user.setEmail(null);
        return user;
    }
    public static UserAdminDto userEntityToUserAdminDto(UserEntity userEntity){
    UserAdminDto userAdminResponse = new UserAdminDto();
        userAdminResponse.setId(userEntity.getUserId());
        userAdminResponse.setIsAuthorized(userEntity.getIsAuthorized() != null);
        userAdminResponse.setLastName(userEntity.getLastName());
        userAdminResponse.setPassword(userEntity.getPassword());
        userAdminResponse.setFirstName(userEntity.getFirstName());
        userAdminResponse.setDisplay(userEntity.getFirstName() + " " + userEntity.getLastName());
        userAdminResponse.setPoster(userEntity.getPoster());
        userAdminResponse.setCreatedAt(userEntity.getCreatedAt());
        userAdminResponse.setAvatar(userEntity.getAvatar());
        userAdminResponse.setPhone(userEntity.getPhone());
        userAdminResponse.setEmail(userEntity.getEmail());
        userAdminResponse.setAvatar(userEntity.getAvatar());
        userAdminResponse.setUpdatedAt(userEntity.getUpdatedAt());
        userAdminResponse.setUsername(userEntity.getUsername());
        userAdminResponse.setRole(userEntity.getRole());
        userAdminResponse.setStatus(userEntity.getStatus());
        userAdminResponse.setInformation(UserInfoMapper.userInfoEntityToUserInfoResponse(userEntity.getUserInfo()));
        return  userAdminResponse;
    }
    public static UserEntity userAdminDtoToUserEntity(UserAdminDto userAdminDto,UserEntity userEntity){
        userEntity.setUserId(userAdminDto.getId());
        userEntity.setIsAuthorized(userAdminDto.getIsAuthorized() != null);
        userEntity.setLastName(userAdminDto.getLastName());
        userEntity.setFirstName(userAdminDto.getFirstName());
        userEntity.setPoster(userAdminDto.getPoster());
        userEntity.setPassword(userAdminDto.getPassword());
        userEntity.setCreatedAt(userAdminDto.getCreatedAt());
        userEntity.setAvatar(userAdminDto.getAvatar());
        userEntity.setPhone(userAdminDto.getPhone());
        userEntity.setEmail(userAdminDto.getEmail());
        userEntity.setAvatar(userAdminDto.getAvatar());
        userEntity.setUpdatedAt(userAdminDto.getUpdatedAt());
        userEntity.setUsername(userAdminDto.getUsername());
        userEntity.setRole(userAdminDto.getRole());
        userEntity.setStatus(userAdminDto.getStatus());
        userEntity.setIsDeleted(userAdminDto.getIsDeleted());
        return  userEntity;
    }
    public static List<UserAdminDto> UserEntityListToAdminDtoList(List<UserEntity> dtoList) {
        return dtoList.stream()
                .map(UserMapper::userEntityToUserAdminDto)
                .collect(Collectors.toList());
    }

    public static UserResponse userInfoToUserResponse(UserInfoEntity userInfo) {

        UserEntity userEntity = userInfo.getUserEntity();
        UserResponse userResponse = new UserResponse();
        userResponse.setId(userEntity.getUserId());
        userResponse.setIsAuthorized(userEntity.getIsAuthorized());
        userResponse.setLastName(userEntity.getLastName());
        userResponse.setFirstName(userEntity.getFirstName());
        userResponse.setDisplay(userEntity.getFirstName() + " " + userEntity.getLastName());
        userResponse.setPoster(userEntity.getPoster());
        userResponse.setAvatar(userEntity.getAvatar());
        userResponse.setPhone(userEntity.getPhone());
        userResponse.setEmail(userEntity.getEmail());
        userResponse.setAvatar(userEntity.getAvatar());
        userResponse.setUsername(userEntity.getUsername());
        userResponse.setId(userEntity.getUserId());
        UserInfoResponse info = UserInfoMapper.userInfoEntityToUserInfoResponse(userInfo);
        userResponse.setInformation(info);


        return  userResponse;
    }

}
