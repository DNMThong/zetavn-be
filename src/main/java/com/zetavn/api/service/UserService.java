package com.zetavn.api.service;

import com.zetavn.api.enums.RoleEnum;
import com.zetavn.api.enums.UserStatusEnum;
import com.zetavn.api.model.dto.UserAdminDto;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.request.UserUpdateRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.UserContactResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.payload.response.UserResponse;
import org.springframework.data.domain.Page;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    ApiResponse<?> create(SignUpRequest signUpRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    ApiResponse<UserResponse> getUserByEmail(String email);

    List<UserResponse> getAllUsers();

    UserResponse update(String userId, UserUpdateRequest userUpdateRequest);

    ApiResponse<UserResponse> remove(String userId);

    ApiResponse<UserResponse> getUserByEmailOrUsername(String username, String email);

    ApiResponse<?> getAllUsersByKeyword(String sourceId, String keyword, Integer pageNumber, Integer pageSize);

    ApiResponse<?> getAllFriendsByKeyword(String sourceId, String keyword, Integer pageNumber, Integer pageSize);

    ApiResponse<?> getStrangersByKeyword(String sourceId, String keyword, Integer pageNumebr, Integer pageSize);

    ApiResponse<UserResponse> updateAvatar(String sourceId, String avatarPath);

    ApiResponse<UserResponse> updatePoster(String sourceId, String avatarPath);

    ApiResponse<List<UserContactResponse>> getUserContacts(String userId);

    ApiResponse<?> getAllUserForAdminByStatus(String status, Integer pageNumber, Integer pageSize);

    ApiResponse<?> pageableUserForAdmin(UserStatusEnum userStatusEnum, Integer pageNumber, Integer pageSize);

    ApiResponse<?> getAllUserForAdmin(Integer pageNumber, Integer pageSize);

    ApiResponse<?> updateForAdmin(UserAdminDto userAdminDto);

    ApiResponse<?> createForAdmin(UserAdminDto userAdminDto);

    ApiResponse<?> removeForAdmin(String id, boolean isDeleted);

    ApiResponse<?> getOneUserForAdmin(String id);

    ApiResponse<?> lockUserAccountForAdmin(String id, UserStatusEnum status, RoleEnum role);
}
