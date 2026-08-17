package com.dzy.userservice8092.controller;

import com.dzy.common.entity.ResultJSON;
import com.dzy.common.entity.User;
import com.dzy.userservice8092.dto.ChangePasswordRequest;
import com.dzy.userservice8092.dto.LoginRequest;
import com.dzy.userservice8092.dto.RegisterRequest;
import com.dzy.userservice8092.dto.TokenPair;
import com.dzy.userservice8092.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RefreshScope
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 登录接口（白名单，无需 X-User-Id）
    @PostMapping("/login")
    public ResultJSON login(@Valid @RequestBody LoginRequest request) {
        TokenPair data=userService.login(request);
        return ResultJSON.success(data);
    }

    // 注册接口（白名单）
    @PostMapping
    public ResultJSON register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResultJSON.success(user);
    }

    // 获取当前用户信息（需要登录）
    @GetMapping("/me")
    public ResultJSON getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        User user = userService.getCurrentUser(userId);
        return ResultJSON.success(user);
    }

    // 修改密码（需要登录）
    @PutMapping("/me/password")
    public ResultJSON changePassword(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody ChangePasswordRequest request){
        userService.changePassword(userId,request);
        return ResultJSON.success();
    }
}