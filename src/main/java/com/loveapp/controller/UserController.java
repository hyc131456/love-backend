package com.loveapp.controller;

import com.loveapp.common.Result;
import com.loveapp.dto.LoginDTO;
import com.loveapp.dto.UserDTO;
import com.loveapp.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 微信登录
     */
    @PostMapping("/wxLogin")
    public Result<LoginDTO> wxLogin(@RequestParam String code) {
        LoginDTO dto = userService.wxLogin(code);
        return Result.success(dto);
    }
    
    /**
     * 模拟登录（开发测试用）
     */
    @PostMapping("/mockLogin")
    public Result<LoginDTO> mockLogin(@RequestParam(defaultValue = "test_openid") String openid) {
        LoginDTO dto = userService.mockLogin(openid);
        return Result.success(dto);
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<LoginDTO> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String nickname) {
        LoginDTO dto = userService.register(username, password, nickname);
        return Result.success(dto);
    }
    
    /**
     * 用户名密码登录
     */
    @PostMapping("/login")
    public Result<LoginDTO> login(
            @RequestParam String username,
            @RequestParam String password) {
        LoginDTO dto = userService.login(username, password);
        return Result.success(dto);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<UserDTO> getProfile() {
        UserDTO dto = userService.getCurrentUser();
        return Result.success(dto);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody @Valid UserDTO dto) {
        userService.updateProfile(dto);
        return Result.success();
    }
}
