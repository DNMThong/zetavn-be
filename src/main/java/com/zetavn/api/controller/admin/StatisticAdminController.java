package com.zetavn.api.controller.admin;

import com.zetavn.api.model.mapper.PostMapper;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.payload.request.StatisticRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.repository.PostRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v0/admins/statistics")
public class StatisticAdminController {
    @Autowired
    StatisticService statisticService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;


    @GetMapping()
    public ApiResponse<?>getStatistic(@RequestParam(required = false) @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate startDate,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate endDate){
            return statisticService.getStatistic(startDate,endDate);
    }

    @GetMapping("/users")
    public ApiResponse<?> statisticsUsers(@RequestParam(required = false) LocalDate startDate,
                                          @RequestParam(required = false) LocalDate endDate,
                                          @RequestParam(defaultValue = "0") Integer pageNumber,
                                          @RequestParam(defaultValue = "5") Integer pageSize) {

        return statisticService.statisticCreateAtUsers(startDate,endDate, pageNumber, pageSize);
    }

    @GetMapping("/posts")
    public ApiResponse<?> statisticsPosts(@RequestParam(required = false) LocalDate startDate,
                                          @RequestParam(required = false) LocalDate endDate,
                                          @RequestParam(defaultValue = "0") Integer pageNumber,
                                          @RequestParam(defaultValue = "5") Integer pageSize) {

        return statisticService.statisticCreateAtPosts(startDate,endDate, pageNumber, pageSize);
    }

    @GetMapping("/posts/popular")
    public ApiResponse<?> statisticsPostPopular(){
        Pageable pageable = PageRequest.of(0, 10);
        return ApiResponse.success(HttpStatus.OK,"success", PostMapper.entityListToDtoList(postRepository.findTop10PostsByCommentAndLike(pageable).getContent()));
    }
    @GetMapping("/users/popular")
    public ApiResponse<?> statisticsUserPopular(){
        Pageable pageable = PageRequest.of(0, 2);
        return ApiResponse.success(HttpStatus.OK,"success", UserMapper.UserEntityListToAdminDtoList(userRepository.findTopFollowedUsers(pageable).getContent()));
    }

}
