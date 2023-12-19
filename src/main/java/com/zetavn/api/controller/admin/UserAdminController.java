package com.zetavn.api.controller.admin;

import com.zetavn.api.enums.RoleEnum;
import com.zetavn.api.enums.UserStatusEnum;
import com.zetavn.api.model.dto.UserAdminDto;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/admins/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserAdminController {
    @Autowired
    UserService userService;

    @GetMapping()
    public ApiResponse<?> getAllUser(
            @RequestParam(name = "status", defaultValue = " ", required = false) String status,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return userService.getAllUserForAdminByStatus(status, pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getOneUser(@PathVariable String id) {
        return userService.getOneUserForAdmin(id);
    }

    @PostMapping()
    public ApiResponse<?> create(@RequestBody UserAdminDto userAdminDto) {
        return userService.createForAdmin(userAdminDto);
    }

    @PutMapping()
    public ApiResponse<?> update(@RequestBody UserAdminDto userAdminDto) {
        return userService.updateForAdmin(userAdminDto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> remove(@PathVariable String id, @RequestBody boolean isDeleted) {
        return userService.removeForAdmin(id, isDeleted);
    }


    @PutMapping("/lock/{id}")
    public ApiResponse<?> lockUserAccount(
            @PathVariable String id,
            @RequestParam UserStatusEnum status,
            @RequestParam RoleEnum role
    ) {
        return userService.lockUserAccountForAdmin(id, status, role);
    }
}
