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
import com.zetavn.api.payload.response.StatisticsOneDayResponse;
import com.zetavn.api.payload.response.StatisticsResponse;
import com.zetavn.api.repository.*;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StatisticServiceImpl implements StatisticService {
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostLikeRepository postLikeRepository;

    @Autowired
    PostSavedRepository postSavedRepository;

    @Autowired
    FollowRepository followRepository;

    @Override
    public ApiResponse<?> statisticCreateAtUsers(LocalDate startDate, LocalDate endDate,Integer pageNumber,Integer pageSize) {
       if(startDate == null|| endDate == null){
           startDate = LocalDate.now();
           endDate = LocalDate.now();
       }
        if (!ValidateData(startDate,endDate)) {
            throw new IllegalArgumentException("Start day have to less than or equal end day");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<UserEntity> users = userRepository.statisticCreatAtUser(startDate,endDate, pageable);
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
    public ApiResponse<?> statisticCreateAtPosts(LocalDate startDate, LocalDate endDate, Integer pageNumber, Integer pageSize) {
        if(startDate==null|| endDate==null){
            startDate = LocalDate.now();
            endDate = LocalDate.now();
        }
        if (!ValidateData(startDate,endDate)) {
            throw new IllegalArgumentException("Start day have to less than or equal end day");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<PostEntity> posts = postRepository.statisticCreatAtPosts(startDate,endDate, pageable);
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

    @Override
    public ApiResponse<?> getStatistic(LocalDate startDate, LocalDate endDate) {
        if(startDate==null|| endDate==null){
            startDate = LocalDate.now();
            endDate = LocalDate.now();
        }
        if (!ValidateData(startDate,endDate)) {
            throw new IllegalArgumentException("Start day have to less than or equal end day");
        }
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        List<StatisticsOneDayResponse> list = new ArrayList<>();
        long daysBetween = ChronoUnit.DAYS.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        for (long i = 0; i <= daysBetween; i++) {
            StatisticsOneDayResponse response = new StatisticsOneDayResponse();
            response.setDate(startDate);
            response.setNewUser(userRepository.countUsersInDateRange(startDate,startDate));
            response.setNewPost(postRepository.countPostsInDateRange(startDate,startDate));
            response.setTotalLikePost(postLikeRepository.countLikesInDateRange(startDate,endDate));
            response.setTotalBookmarkPost(postSavedRepository.countPostsSavedInDateRange(startDate,startDate));
            response.setTotalCommentPost(commentRepository.countCommentInDateRange(startDate,startDate));
            response.setTotalFollower(followRepository.countFollowInDateRange(startDate,startDate));
            list.add(response);
            startDate = startDate.plusDays(1);
        }
        statisticsResponse.setData(list);
        return ApiResponse.success(HttpStatus.OK,"success",statisticsResponse);
    }

    public boolean ValidateData(LocalDate startDate, LocalDate endDate){
        return !endDate.isBefore(startDate);
    }
}
