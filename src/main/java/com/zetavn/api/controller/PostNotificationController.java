package com.zetavn.api.controller;

import com.zetavn.api.payload.request.UpdateIsReadNotificationResponse;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/notifications")
public class PostNotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("")
    public ApiResponse<?> list(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                               @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        try {
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ApiResponse.success(HttpStatus.OK, "", notificationService.listNotification(id, pageNumber, pageSize));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/read")
    public ApiResponse<?> updateIsReadNotification(@RequestBody UpdateIsReadNotificationResponse updateIsReadNotificationResponse) {
        Boolean isSuccess = notificationService.updateIsReadNotification(updateIsReadNotificationResponse.getIds());
        if(isSuccess) return ApiResponse.success(HttpStatus.OK,"",null);
        return ApiResponse.error(HttpStatus.BAD_REQUEST,"ERROR",null);
    }
}
