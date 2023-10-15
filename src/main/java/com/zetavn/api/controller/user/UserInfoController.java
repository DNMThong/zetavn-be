package com.zetavn.api.controller.user;

import com.zetavn.api.payload.request.UserInfoRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping("/api/v0/user-info")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/{userId}")
    public ApiResponse<?> getUserInfo(@PathVariable(name = "userId", required = true) Optional<String> userId) {
        return ApiResponse.success(HttpStatus.OK, "userId", userId);
    }

    @PutMapping("/{userId}")
    public ApiResponse<?> updateUserInfo(@PathVariable(name = "userId", required = true) Optional<String> userId, @RequestBody UserInfoRequest userInfoRequest) {
        if (userId.isEmpty())
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Missing userId");
        return userInfoService.update(userId.orElse(null), userInfoRequest);
    }
}
