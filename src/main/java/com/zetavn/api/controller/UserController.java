package com.zetavn.api.controller;

import com.zetavn.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v0/users")
public class UserController {
    @Autowired
    UserService userService;

}
