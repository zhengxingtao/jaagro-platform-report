package com.jaagro.report.web.controller;

import com.jaagro.report.api.dto.UserLoginDto;
import com.jaagro.report.api.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tony
 */
@RestController
public class UserLoginController {

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping("/userLogin")
    public void createUserLogin(UserLoginDto userLoginDto) {
        userLoginService.createUserLogin(userLoginDto);
    }
}
