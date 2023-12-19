package com.zetavn.api.controller.admin;

import com.zetavn.api.payload.request.SignInRequest;
import com.zetavn.api.payload.request.SignUpRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.impl.AuthServiceImpl;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/v0/admins/auth")
public class AuthAdminController {

    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/login")
    public ApiResponse<?> loginAdmin(@RequestBody SignInRequest signInRequest, HttpServletResponse request) throws Exception {
        return authService.loginForAdmin(signInRequest, request);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<?> createAdminAccount(@RequestBody SignUpRequest signUpRequest) {
        return authService.createForAdmin(signUpRequest);
    }
}
