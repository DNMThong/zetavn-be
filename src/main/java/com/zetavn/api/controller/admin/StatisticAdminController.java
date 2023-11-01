package com.zetavn.api.controller.admin;

import com.zetavn.api.payload.request.StatisticRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/admins/statistics")
public class StatisticAdminController {
    @Autowired
    StatisticService statisticService;

    @GetMapping("/users")
    public ApiResponse<?> statisticsUsers(@RequestBody StatisticRequest statisticRequest,
                                          @RequestParam (defaultValue = "0") Integer pageNumber,
                                          @RequestParam (defaultValue = "5") Integer pageSize){
        return statisticService.statisticCreateAtUsers(statisticRequest,pageNumber,pageSize);
    }

    @GetMapping("/posts")
    public ApiResponse<?> statisticsPosts(@RequestBody StatisticRequest statisticRequest,
                                          @RequestParam (defaultValue = "0") Integer pageNumber,
                                          @RequestParam (defaultValue = "5") Integer pageSize){
        return statisticService.statisticCreateAtPosts(statisticRequest,pageNumber,pageSize);
    }

}
