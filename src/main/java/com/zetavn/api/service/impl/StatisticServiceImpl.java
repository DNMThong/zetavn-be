package com.zetavn.api.service.impl;

import com.zetavn.api.model.dto.PostAdminDto;
import com.zetavn.api.model.dto.UserAdminDto;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.PostMapper;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.StatisticRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.repository.PostRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StatisticServiceImpl implements StatisticService {
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PostRepository postRepository;

    @Override
    public ApiResponse<?> statisticCreateAtUsers(StatisticRequest statisticRequest,Integer pageNumber,Integer pageSize) {
        if (!ValidateData(statisticRequest)) {
            throw new IllegalArgumentException("Start day have to less than or equal end day");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<UserEntity> users = userRepository.statisticCreatAtUser(statisticRequest.getStartDay(),statisticRequest.getEndDay(), pageable);
        if (pageNumber > users.getTotalPages()) {
            log.error("Error Logging: pageNumber: {} is out of total_page: {}", pageNumber, users.getNumber());
            throw new InvalidParameterException("pageNumber is out of total Page");
        }
        try {
            List<UserEntity> userEntities = users.getContent();
            List<UserAdminDto> userAdminDtos = userEntities.stream().map(UserMapper::userEntityToUserAdminDto).toList();
            Paginate<List<UserAdminDto>> dataResponse = new Paginate<>();
            dataResponse.setData(userAdminDtos);
            dataResponse.setPageNumber(users.getNumber());
            dataResponse.setPageSize(users.getSize());
            dataResponse.setTotalElements(users.getTotalElements());
            dataResponse.setTotalPages(users.getTotalPages());
            dataResponse.setLastPage(users.isLast());
            return ApiResponse.success(HttpStatus.OK, "Success", dataResponse);
        } catch (Exception e) {
            log.error("Error Logging: pageNumber: {}, pageSize: {}, pageable: {}, error_message: {}", pageNumber, pageSize, pageable, e.getMessage());
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
        }
    }

    @Override
    public ApiResponse<?> statisticCreateAtPosts(StatisticRequest statisticRequest, Integer pageNumber, Integer pageSize) {
        if (!ValidateData(statisticRequest)) {
            throw new IllegalArgumentException("Start day have to less than or equal end day");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<PostEntity> posts = postRepository.statisticCreatAtPosts(statisticRequest.getStartDay(),statisticRequest.getEndDay(), pageable);
        if (pageNumber > posts.getTotalPages()) {
            log.error("Error Logging: pageNumber: {} is out of total_page: {}", pageNumber, posts.getNumber());
            throw new InvalidParameterException("pageNumber is out of total Page");
        }
        try {
            List<PostEntity> postEntities = posts.getContent();
            List<PostAdminDto> postAdminDtos = postEntities.stream().map(PostMapper::entityToPostAdminDto).toList();
            Paginate<List<PostAdminDto>> dataResponse = new Paginate<>();
            dataResponse.setData(postAdminDtos);
            dataResponse.setPageNumber(posts.getNumber());
            dataResponse.setPageSize(posts.getSize());
            dataResponse.setTotalElements(posts.getTotalElements());
            dataResponse.setTotalPages(posts.getTotalPages());
            dataResponse.setLastPage(posts.isLast());
            return ApiResponse.success(HttpStatus.OK, "Success", dataResponse);
        } catch (Exception e) {
            log.error("Error Logging: pageNumber: {}, pageSize: {}, pageable: {}, error_message: {}", pageNumber, pageSize, pageable, e.getMessage());
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Param");
        }
    }
    public boolean ValidateData(StatisticRequest statisticRequest){
        if (statisticRequest.getStartDay() == null || statisticRequest.getEndDay() == null) {
            return false;
        }
        return !statisticRequest.getEndDay().isBefore(statisticRequest.getStartDay());
    }
}
