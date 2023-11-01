package com.zetavn.api.service;

import com.zetavn.api.payload.request.StatisticRequest;
import com.zetavn.api.payload.response.ApiResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface StatisticService {
    ApiResponse<?>statisticCreateAtUsers(StatisticRequest statisticRequest, Integer pageSize, Integer pageNumber);
    ApiResponse<?>statisticCreateAtPosts(StatisticRequest statisticRequest,Integer pageSize, Integer pageNumber);

}
